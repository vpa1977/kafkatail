using Confluent.Kafka;
using System;
using System.Collections.Concurrent;
using System.Threading;
using System.Threading.Tasks;

#nullable enable

namespace SyncOverAsync
{
    // Implement sync over async pattern
    // Not to be used together with database transactions
    public class SyncOverAsync<Key, Request,Response>  : IDisposable
    {
        private string _requestTopic;
        private IConsumer<Key, Response> _consumer;
        private IProducer<Key, Request> _producer;
        private CancellationTokenSource _cancellationTokenSource;
        private Thread _notificationThread;
        private record PendingCall(Guid g, SemaphoreSlim semaphore, Response? response, Func<Response, bool> filter);
        private ConcurrentDictionary<Guid, PendingCall> _calls = new();
        public SyncOverAsync(string requestTopic, string responseTopic, string group, string bootstrap)
        {
            _requestTopic = requestTopic;
            _consumer = new ConsumerBuilder<Key, Response>(new ConsumerConfig()
            {
                AutoOffsetReset = AutoOffsetReset.Latest,
                GroupId = group,
                GroupInstanceId = group,
                BootstrapServers = bootstrap
            })
            .Build();

            _producer = new ProducerBuilder<Key, Request>(new ProducerConfig()
            {
                BootstrapServers = bootstrap
            }).Build();

            _consumer.Subscribe(responseTopic);
            _cancellationTokenSource = new CancellationTokenSource();
            var token = _cancellationTokenSource.Token;
            _notificationThread = new Thread(() =>{
                while (!token.IsCancellationRequested)
                {
                    try
                    {
                        var response = _consumer.Consume(token);
                        if (!token.IsCancellationRequested && response != null)
                        {
                            HandleResponse(response.Message.Value);
                        }
                        _consumer.Commit();
                    }
                    catch (KafkaException){ }
                    catch (OperationCanceledException) { }
                }
                _consumer.Close();
            });
            _notificationThread.Start();
        }

        private void HandleResponse(Response message)
        {
            var values = _calls.Values;
            foreach (var v in values)
            {
               if (v.filter(message))
               {
                    _calls.TryUpdate(v.g, new(v.g, v.semaphore, message, v.filter), v);
                    v.semaphore.Release();
               }
            }
        }

        public async Task<Response?> Invoke(Key key, Request request, Func<Response, bool> responseFilter, int timeoutMs)
        {
            var callId = Guid.NewGuid();
            using var semaphore = new SemaphoreSlim(0);
            PendingCall call = new(callId, semaphore, default(Response), responseFilter);
            if (!_calls.TryAdd(callId, call))
                throw new ArgumentException("Unable to add call id");
            _producer.Produce(_requestTopic, new Message<Key, Request>()
            {
                Key = key,
                Value = request
            });
            var result = await semaphore.WaitAsync(timeoutMs);
            if (_calls.TryRemove(callId, out var responseResult))
                return responseResult.response;
            return default(Response);
        }

        public void Dispose()
        {
            _cancellationTokenSource.Cancel();
            _notificationThread.Join();
            _cancellationTokenSource.Dispose();
            _producer.Dispose();
            _consumer.Dispose();
        }
    }
}
