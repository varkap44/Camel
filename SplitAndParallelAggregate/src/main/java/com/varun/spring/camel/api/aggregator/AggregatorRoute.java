package com.varun.spring.camel.api.aggregator;

import java.util.Date;
import java.util.Random;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class AggregatorRoute extends RouteBuilder {
	
	
	final String CORRELATION_ID = "correlationId";
	Random random  = new Random();

	@Override
	public void configure() throws Exception {
		
		from("timer:insurance?period=200")
			.process(exchange -> {
				
				Message message = exchange.getMessage();
				message.setHeader(CORRELATION_ID, random.nextInt(4));
				message.setBody(new Date());
			})
			.aggregate(header(CORRELATION_ID), new MyAggregationStrategy())
			.completionSize(3)
			.log(LoggingLevel.INFO,"${header." + CORRELATION_ID + "} ${body}");
		
		
	}

}
