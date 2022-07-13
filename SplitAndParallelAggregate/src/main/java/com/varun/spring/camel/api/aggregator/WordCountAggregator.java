package com.varun.spring.camel.api.aggregator;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

@Component
public class WordCountAggregator implements AggregationStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		
		if (Objects.isNull(oldExchange)) {
			return newExchange;
		}
		
		Map<String, Long> oldBody = oldExchange.getIn().getBody(ConcurrentHashMap.class);
		Map<String, Long> newBody = newExchange.getIn().getBody(ConcurrentHashMap.class);
		
		Map<String, Long> aggBody = Stream.of(oldBody, newBody).flatMap(m -> m.entrySet().stream())
			       .collect(Collectors.toConcurrentMap(Entry::getKey, Entry::getValue, Long::sum));
		
		oldExchange.getIn().setBody(aggBody);
		
		return oldExchange;
	}
		
}
