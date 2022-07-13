package com.varun.spring.camel.api.processor;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MyFileProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		String body = exchange.getIn().getBody(String.class);
		Map<String, Long> wordCount = new ConcurrentHashMap<>();
		wordCount = Arrays.stream(body.split(" "))
				.map(word -> word.toLowerCase().trim())
				.filter(word -> !word.isEmpty())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		exchange.getIn().setBody(wordCount);
		
	}

}
