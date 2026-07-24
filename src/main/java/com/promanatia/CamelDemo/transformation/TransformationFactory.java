package com.promanatia.CamelDemo.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TransformationFactory {

	private final Map<String, TransformationRule> ruleMap = new HashMap<>();

	public TransformationFactory(List<TransformationRule> rules) {

		for (TransformationRule rule : rules) {
			ruleMap.put(rule.getRuleCode().toUpperCase(), rule);
		}
	}

	public TransformationRule getRule(String ruleCode) {

		if (ruleCode == null) {
			return null;
		}

		return ruleMap.get(ruleCode.toUpperCase());
	}

}