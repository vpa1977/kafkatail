/* Copyright 2022 The Kafkatail Authors*/
package com.kafkatail.service;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.annotation.PreDestroy;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaAdmin {

  private AdminClient _admin;

  public KafkaAdmin(@Value("${kafka.bootstrap}") String bootstrap) {
    var properties = new Properties();
    properties.put("bootstrap.servers", bootstrap);
    properties.put("connections.max.idle.ms", 10000);
    properties.put("request.timeout.ms", 5000);
    _admin = AdminClient.create(properties);
  }

  public Set<String> listTopics() throws ExecutionException, InterruptedException {
    return _admin.listTopics().names().get();
  }

  public String removeTopic(String topic) throws InterruptedException, ExecutionException {
    var result = _admin.deleteTopics(Arrays.asList(topic)).topicNameValues().get(topic);
    result.get();
    return topic;
  }

  public Uuid createTopic(String topic, int partitions, short replicationFactor)
      throws ExecutionException, InterruptedException {
    var newTopic = new NewTopic(topic, partitions, replicationFactor);
    return _admin.createTopics(Arrays.asList(newTopic)).topicId(topic).get();
  }

  @PreDestroy
  public void dispose() {
    if (_admin == null) return;
    _admin.close();
  }
}
