package com.kafkatail.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafkatail.model.Topics;
import com.kafkatail.service.KafkaAdmin;
import com.kafkatail.service.KafkaMessageService;

@RestController
public class RootController {
	
	@Autowired
	private KafkaAdmin _admin;
	
	@Autowired
	private KafkaMessageService _messageService;
	
	@GetMapping("/")
	public String index() {
		return "(kafkatail)";
	}
	
	@GetMapping("/topics")
	public Topics topics() throws InterruptedException, ExecutionException {
		var topics = new Topics();
		topics.TopicList = new ArrayList<String>(_admin.listTopics());
		return topics;
	}
	
	@DeleteMapping("/topics/remove")
	public  String removeTopic(String topic) throws InterruptedException, ExecutionException {
		return _admin.removeTopic(topic);
	}
	
	@PutMapping("/topics/create")
	public HashMap<String, Uuid> addTopic(String topic, int partitions, short replicationFactor)  throws InterruptedException, ExecutionException  {
		var uuid = _admin.createTopic(topic, partitions, replicationFactor);
		var map = new HashMap<String, Uuid>();
		map.put(topic, uuid);
		return map;
	}
	
	@PostMapping("/topics/subscribe")
	public void subscribeTopic(String topic)  throws InterruptedException, ExecutionException  {
		_messageService.subscribe(topic);
	}

	@PostMapping("/topics/unsubscribe")
	public void unsubscribeTopic(String topic)  throws InterruptedException, ExecutionException  {
		_messageService.unsubscribe(topic);
	}
	
	@GetMapping("/topics/subscriptions")
	public Set<String> subscriptions(){
		return _messageService.subscriptions();
	}
}
