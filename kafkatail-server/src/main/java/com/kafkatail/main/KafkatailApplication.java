package com.kafkatail.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com/kafkatail")
public class KafkatailApplication {
	
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
     return new PropertySourcesPlaceholderConfigurer();
  }

   public static void main(String[] args) {
     SpringApplication.run(KafkatailApplication.class, args);
   }

}
