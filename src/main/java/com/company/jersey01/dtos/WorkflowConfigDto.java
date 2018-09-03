package com.company.jersey01.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
//@EqualsAndHashCode(callSuper=true)
public class WorkflowConfigDto /*extends BaseTenantDatabaseItemDto*/ {
  private String key;
  private String value;
  private String type;
  private Boolean sensitive;
  private Long workflowId;
  private Long workflowConfigDefinitionId;

  @Builder
  public WorkflowConfigDto(Long id, Timestamp createdAt, Timestamp updatedAt, String tenantId,
                           Long tenantUserId, String key, String value, String type, Boolean sensitive, Long workflowId,
                           Long workflowConfigDefinitionId) {
    //super(id, createdAt, updatedAt, tenantId, tenantUserId);
    this.key = key;
    this.value = value;
    this.type = type;
    this.sensitive = sensitive;
    this.workflowId = workflowId;
    this.workflowConfigDefinitionId = workflowConfigDefinitionId;
  }
}
