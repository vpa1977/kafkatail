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
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class TopicsServiceRpc extends TopicsServiceImplBase {

  @Override
  public void getTopics(Void request, StreamObserver<Topics> responseObserver) {
    // TODO Auto-generated method stub
    super.getTopics(request, responseObserver);
  }

  @Override
  public void removeTopic(TopicRemoveRequest request, StreamObserver<Topic> responseObserver) {
    // TODO Auto-generated method stub
    super.removeTopic(request, responseObserver);
  }

  @Override
  public void createTopic(TopicCreateRequest request, StreamObserver<Topic> responseObserver) {
    // TODO Auto-generated method stub
    super.createTopic(request, responseObserver);
  }

  @Override
  public void subscribe(TopicSubscribeRequest request, StreamObserver<Void> responseObserver) {
    // TODO Auto-generated method stub
    super.subscribe(request, responseObserver);
  }

  @Override
  public void unsubscribe(TopicUnsubscribeRequest request, StreamObserver<Void> responseObserver) {
    // TODO Auto-generated method stub
    super.unsubscribe(request, responseObserver);
  }

  @Override
  public void subscriptions(Void request, StreamObserver<Topics> responseObserver) {
    // TODO Auto-generated method stub
    super.subscriptions(request, responseObserver);
  }
}
