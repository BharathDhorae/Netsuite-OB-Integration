package com.promanatia.CamelDemo.utility;

import org.springframework.stereotype.Service;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.transformation.TransformationFactory;
import com.promanatia.CamelDemo.transformation.TransformationRule;

@Service
public class TransformationUtil {

	private final TransformationFactory factory;

	public TransformationUtil(TransformationFactory factory) {
		this.factory = factory;
	}

	public RowContext createContext(String[] headers, String[] columns) {

		return new RowContext(headers, columns);
	}

	public String applyTransformation(RowContext row, FieldMappingEntity mapping) {

		String ruleCode = mapping.getTransformationRuleCode();

		if (ruleCode == null || ruleCode.isBlank()) {
			return row.get(mapping.getSourceColumn());
		}

		TransformationRule rule = factory.getRule(ruleCode);

		if (rule == null) {

			throw new IllegalArgumentException("Unsupported Rule : " + ruleCode);
		}

		return rule.transform(row, mapping);
	}

}