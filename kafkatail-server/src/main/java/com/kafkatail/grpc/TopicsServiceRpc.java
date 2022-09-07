/* Copyright 2022 The Kafkatail Authors*/
package com.kafkatail.grpc;

import com.kafkatail.Tailgrpc.Topic;
import com.kafkatail.Tailgrpc.TopicCreateRequest;
import com.kafkatail.Tailgrpc.TopicRemoveRequest;
import com.kafkatail.Tailgrpc.TopicSubscribeRequest;
import com.kafkatail.Tailgrpc.TopicUnsubscribeRequest;
import com.kafkatail.Tailgrpc.Topics;
import com.kafkatail.Tailgrpc.Void;
import com.kafkatail.TopicsServiceGrpc.TopicsServiceImplBase;
import com.kafkatail.service.KafkaAdmin;
import com.kafkatail.service.KafkaMessageService;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ExecutionException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class TopicsServiceRpc extends TopicsServiceImplBase {

  @Autowired private KafkaMessageService _messageService;
  @Autowired private KafkaAdmin _admin;

  @Override
  public void getTopics(Void request, StreamObserver<Topics> responseObserver) {
    var topicsBuilder = Topics.newBuilder();
    try {
      for (var topic : _admin.listTopics())
        topicsBuilder.addTopic(Topic.newBuilder().setName(topic).build());
      var message = topicsBuilder.build();
      responseObserver.onNext(message);
      responseObserver.onCompleted();
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace(); // TODO: add logger
      responseObserver.onError(e);
    }
  }

  @Override
  public void removeTopic(TopicRemoveRequest request, StreamObserver<Topic> responseObserver) {
    var topic = request.getTopic();
    try {
      var response = Topic.newBuilder().setName(_admin.removeTopic(topic.getName())).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  @Override
  public void createTopic(TopicCreateRequest request, StreamObserver<Topic> responseObserver) {
    var topic = request.getTopic();
    try {
      var uuid =
          _admin.createTopic(
              topic.getName(), request.getPartitions(), (short) request.getReplicationFactor());
      var response = Topic.newBuilder().setName(uuid != null ? topic.getName() : null).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  @Override
  public void subscribe(TopicSubscribeRequest request, StreamObserver<Void> responseObserver) {
    _messageService.subscribe(request.getTopic().getName());
    responseObserver.onCompleted();
  }

  @Override
  public void unsubscribe(TopicUnsubscribeRequest request, StreamObserver<Void> responseObserver) {
    _messageService.unsubscribe(request.getTopic().getName());
    responseObserver.onCompleted();
  }

  @Override
  public void subscriptions(Void request, StreamObserver<Topics> responseObserver) {
    var topicsBuilder = Topics.newBuilder();
    for (var topic : _messageService.subscriptions())
      topicsBuilder.addTopic(Topic.newBuilder().setName(topic).build());
    var message = topicsBuilder.build();
    responseObserver.onNext(message);
    responseObserver.onCompleted();
  }
}
