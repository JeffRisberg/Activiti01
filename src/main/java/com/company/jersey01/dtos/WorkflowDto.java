package com.company.jersey01.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
//@EqualsAndHashCode(callSuper = true)
public class WorkflowDto /*extends BaseTenantDatabaseItemDto*/ {
    private String name;
    private String description;
    private Long oAuth2ProviderId;
    private Long workflowDefinitionId;

    @Builder
    public WorkflowDto(Long id, Timestamp createdAt, Timestamp updatedAt, String tenantId,
                       Long tenantUserId, String name, String description,
                       Long oAuth2ProviderId, Long workflowDefinitionId) {
        //super(id, createdAt, updatedAt, tenantId, tenantUserId);
        this.name = name;
        this.description = description;
        this.oAuth2ProviderId = oAuth2ProviderId;
        this.workflowDefinitionId = workflowDefinitionId;
    }
}
