package com.promantia.openbravonetsuiteintegration.utility;

import org.springframework.stereotype.Service;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationFactory;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;

@Service
public class TransformationUtil {

	private final TransformationFactory factory;

	public TransformationUtil(TransformationFactory factory) {
		this.factory = factory;
	}

	public String applyTransformation(RowContext row, FieldMappingDTO mapping) {

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