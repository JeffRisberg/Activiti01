package com.company.jersey01;

import com.company.jersey01.io.WorkflowExecutionResponse;
import lombok.NonNull;

import java.util.Map;

/**
 * API for a Workflow Driver
 */
public interface WorkflowDriver {
  /**
   * Start a new Workflow Execution.
   * @param tenantId Tenant Id
   * @param processName BPMN defined Process Id
   * @return Response from Starting the Workflow
   */
  WorkflowExecutionResponse startWorkflow(@NonNull String tenantId, @NonNull String userId, @NonNull String processName, Map<String, Object> input);

  /**
   * Workflows executes as per BPMN definition on it's own. Until it stops at a UserTask, at which
   * point it requires external Inputs. This API resumes the workflow execution by providing User Inputs.
   * @param tenantId
   * @param processInstanceId
   * @param input
   * @return Workflow Execution Response
   */
  WorkflowExecutionResponse executeUserTask(@NonNull String tenantId, @NonNull String processInstanceId, Map<String, Object> input);

  /**
   * Current Status of a workflow execution (process). This API queries the History for the Process
   * which may have ended/failed etc.
   * @param tenantId
   * @param processInstanceId
   * @return Status of the Process
   */
  WorkflowExecutionResponse status(@NonNull String tenantId, @NonNull String processInstanceId);

  /**
   * Exit the workflow. This can be user triggered or after last step execution.
   * @param tenantId
   * @param processInstanceId
   * @return Response.
   */
  WorkflowExecutionResponse exitWorkflow(@NonNull String tenantId, @NonNull String processInstanceId);
}
