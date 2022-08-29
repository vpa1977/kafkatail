package com.kafkatail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.kafkatail.controller.RootController;
import com.kafkatail.main.KafkatailApplication;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = KafkatailApplication.class)
public class ControllerTests {

	
	@Autowired
	private RootController _controller;
	
	@Test
	public void controllerInstantiatesTest()
	{
		var ent =  _controller.index();
		assertThat(ent).isEqualTo("(kafkatail)");
	}
}
