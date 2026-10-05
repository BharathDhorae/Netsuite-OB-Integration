package com.promanatia.openbravonetsuiteintegration.transformation;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

public interface TransformationRule {

	String getRuleCode();

	String transform(RowContext row, FieldMappingDTO mapping);
}