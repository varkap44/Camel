package com.varun.spring.camel.api.aggregator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.varun.spring.camel.api.processor.MyFileProcessor;

@Component
public class WordCount extends RouteBuilder implements InitializingBean {
	
	ExecutorService exeService;
	
	@Value("${com.varun.spring.camel.api.queue}")
	private String queue;
	
	@Value("${com.varun.spring.camel.api.errorqueue}")
	private String errorQueue;
	
	@Value("${com.varun.spring.camel.api.num}")
	private int numThreads;
	
	public WordCount() {
	}

	@Override
	public void configure() throws Exception {
		
		//Exception Handling
		onException(RuntimeException.class)
		.routeId("errorQueue")
		.handled(true)
	    //.log(LoggingLevel.ERROR, "There is an exception that occured");
		.to("activemq:" + errorQueue)
		.end();
		
		onException(JMSException.class)
			.routeId("ActiveMQ")
			.handled(true)
			.log(LoggingLevel.ERROR, "Please start the active mq on default port")
			.end();
		
		
		//Split the message
		from("file:source?noop=true")
			.routeId("wordCount")
			.multicast()
			.split()
			.tokenize("\n")
			.parallelProcessing()
			//SEDA TO MULTIPLE CHANNELS
			.to("seda:channels?multipleConsumers=true")
			.end();
			
		//Process and aggregate
		from("seda:channels?multipleConsumers=true")
			.routeId("word processor")
			.process(getProcessor())
			//WIRETAP
			.wireTap("direct:tap")
			.aggregate(constant(true), getAggregator())
			.executorService(exeService)
			//.parallelProcessing()
			.completionTimeout(100)
			.convertBodyTo(String.class)
			.to("file:destination?fileName=outputFile.txt")
			.end();
			
			//Get the Activemq server up on default port
			//Queue gets created automatically
		from("seda:channels?multipleConsumers=true")
			.routeId("myQueue")
			.convertBodyTo(String.class)
			//.delay(20000)
			//Uncomment below code to get the exception
//			.process(exchange -> {
//				throw new RuntimeException("The JMS queue is down");
//			})
			.to("activemq:" + queue).end();
			
		//WireTap
		from("direct:tap")
			.routeId("Tapped logger")
			.delay(5000)
			.to("log:splitted_body_is_here")
			.end();
			
		
	}
	
	public Processor getProcessor() {
		return new MyFileProcessor();
	}
	
	public AggregationStrategy getAggregator() {
		return new WordCountAggregator();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.exeService = Executors.newWorkStealingPool(numThreads);
		
	}

}
