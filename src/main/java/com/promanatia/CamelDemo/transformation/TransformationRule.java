package com.promanatia.CamelDemo.transformation;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.utility.RowContext;

public interface TransformationRule {

	String getRuleCode();

	String transform(RowContext row, FieldMappingEntity mapping);
}