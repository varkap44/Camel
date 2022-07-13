package com.varun.spring.camel.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;

@SpringBootApplication
public class SplitAndParallelAggregateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitAndParallelAggregateApplication.class, args);
	}

}
