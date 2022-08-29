/* Copyright 2022 The Kafkatail Authors*/
package com.kafkatail;

import static org.assertj.core.api.Assertions.assertThat;

import com.kafkatail.controller.RootController;
import com.kafkatail.main.KafkatailApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KafkatailApplication.class)
public class ControllerTests {

  @Autowired private RootController _controller;

  @Test
  public void controllerInstantiatesTest() {
    var ent = _controller.index();
    assertThat(ent).isEqualTo("(kafkatail)");
  }
}
