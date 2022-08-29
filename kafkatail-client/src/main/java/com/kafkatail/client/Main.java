/* Copyright 2022 The Kafkatail Authors*/
package com.kafkatail.client;

import com.kafkatail.Tailgrpc.Topic;
import com.kafkatail.Tailgrpc.TopicCreateRequest;
import com.kafkatail.TopicsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Main {
  public static void main(String[] args) {
    ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:9090").usePlaintext().build();
    var blockingStub = TopicsServiceGrpc.newBlockingStub(channel);
    var createReq =
        TopicCreateRequest.newBuilder()
            .setTopic(Topic.newBuilder().setName("test").build())
            .build();

    blockingStub.createTopic(createReq);
  }
}
