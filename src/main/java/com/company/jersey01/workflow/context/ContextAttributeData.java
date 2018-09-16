package com.company.jersey01.workflow.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContextAttributeData {
  private ContextDataType type;
  private Object value;
}
