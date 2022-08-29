package com.kafkatail.service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageService implements Runnable {
	
	private KafkaConsumer<String, byte[]> _consumer;
	private Thread _consumerThread;
	private HashSet<String> _topics;

	public KafkaMessageService(@Value("${kafka.bootstrap}") String bootstrap) {
		
		_topics = new HashSet<String>();
		
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "_kafkaTailGroup");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
	    _consumer = new KafkaConsumer<String, byte[]>(props);
	    _consumerThread = new Thread(this, "consumer");
	    _consumerThread.start();
	}
	
	@PreDestroy
	public void destroy()
	{
		try {
			_consumer.wakeup();
			_consumerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized Set<String> subscriptions() {
		return _consumer.subscription();
	}
	
	public synchronized void unsubscribe(String topic) {
		_topics.remove(topic);
		if (_topics.isEmpty())
			_consumer.unsubscribe();
		else
			_consumer.subscribe(_topics);
	}
	
	public synchronized void subscribe(String topic) {
		_topics.add(topic);
		_consumer.subscribe(_topics);
	}
	
	private synchronized ConsumerRecords<String, byte[]> consume()
	{
		if (_topics.isEmpty())
			return null;
		return _consumer.poll(Duration.ofMillis(100));
	}

	@Override
	public void run() {
		try 
		{
			while (true) {
				process(consume());
			}
		}
		catch (WakeupException ex)
		{
			// ignore wakeup exception - this is app shutting down
		}
	}

	private void process(ConsumerRecords<String, byte[]> consume) {
		if (consume == null || consume.count() == 0)
			return;
		
		
		System.out.println("Processing message");
	}
	
}
