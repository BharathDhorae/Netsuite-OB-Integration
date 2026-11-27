package com.promantia.openbravonetsuiteintegration.transformation;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

public interface TransformationRule {

	String getRuleCode();

	String transform(RowContext row, FieldMappingDTO mapping);
}