package com.company.jersey01.models;

import com.company.jersey01.dtos.WorkflowDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workflow")
public class Workflow extends AbstractTenantDatabaseItem {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

  /*
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "oauth2_provider", updatable = false, insertable = false, nullable = true)
  @JsonIgnore
  private OAuth2Provider oAuth2Provider;
  */

    @Column(name = "oauth2_provider", nullable = true)
    private Long oAuth2ProviderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition", updatable = false, insertable = false)
    @JsonIgnore
    private WorkflowDefinition workflowDefinition;

    @Column(name = "workflow_definition")
    private Long workflowDefinitionId;

    @Builder
    public Workflow(Long id, Timestamp createdAt, Timestamp updatedAt, /*Tenant tenant,*/ String tenantId,
                    /*TenantUser tenantUser,*/ long tenantUserId, String name, String description,
                    /*OAuth2Provider oAuth2Provider,*/ Long oAuth2ProviderId, WorkflowDefinition workflowDefinition,
                    Long workflowDefinitionId) {
        super(id, createdAt, updatedAt, /*tenant,*/ tenantId, /*tenantUser,*/ tenantUserId);
        this.name = name;
        this.description = description;
        //this.oAuth2Provider = oAuth2Provider;
        this.oAuth2ProviderId = oAuth2ProviderId;
        this.workflowDefinition = workflowDefinition;
        this.workflowDefinitionId = workflowDefinitionId;
    }

    public WorkflowDto dto() {
        return WorkflowDto.builder().id(getId()).updatedAt(getUpdatedAt()).createdAt(getCreatedAt())
                .name(getName()).description(getDescription()).oAuth2ProviderId(getOAuth2ProviderId())
                /*.tenantId(getTenantId())*/
                /*.tenantUserId(getTenantUserId())*/
                .workflowDefinitionId(getWorkflowDefinitionId())
                .build();
    }
}
