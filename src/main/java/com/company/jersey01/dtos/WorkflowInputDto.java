package com.company.jersey01.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
//@EqualsAndHashCode(callSuper=true)
public class WorkflowInputDto /*extends BaseTenantDatabaseItemDto*/{
  private String name;
  private String description;
  private String type;
  private Boolean sensitive;
  private String question;
  private Long workflowId;
  private Long workflowInputDefinitionId;

  @Builder
  public WorkflowInputDto(Long id, Timestamp createdAt, Timestamp updatedAt, String tenantId, long tenantUserId, String name, String description,
                          String type, Boolean sensitive,
                          String question, Long workflowId, Long workflowInputDefinitionId) {
    //super(id, createdAt, updatedAt, tenantId, tenantUserId);
    this.name = name;
    this.description = description;
    this.type = type;
    this.sensitive = sensitive;
    this.question = question;
    this.workflowId = workflowId;
    this.workflowInputDefinitionId = workflowInputDefinitionId;
  }
}
