package com.company.jersey01.io;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowExecutionResponse {
    // Tenant Id
    private String tenantId;

    // Process Name (as defined in BPMN xml configuration
    private String processName;

    // Runtime data
    private String processInstanceId;
    private String processInstanceName;
    // Runtime User Message
    private String processInstanceMessage;

    @Builder.Default
    private Boolean userInputError = Boolean.FALSE;

    // Current status (HTTP Statuses)
    @Builder.Default
    private int status = 200;

    // For debugging/logging purpose
    private String statusDetail;

    @Builder.Default
    private Boolean processCompleted = false;

    // Final message. Filled in by Terminal Task
    private String processCompletionMessage;

    // Input params from next task in workflow
    private TaskInputParams taskInputParams;

    //public String json() {
    //    return new Gson().toJson(this);
    //}
}
