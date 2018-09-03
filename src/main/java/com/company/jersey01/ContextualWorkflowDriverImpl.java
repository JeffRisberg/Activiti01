package com.company.jersey01;

import com.company.jersey01.io.WorkflowExecutionResponse;
import com.google.gson.Gson;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This implementation of Workflow Driver - takes input from 'Context' and feeds into Workflow
 * steps.
 * Instead of step by step workflow execution, this implementation can drive a Workflow by
 * multiple inputs available in User's Conversation Context
 */
@Slf4j
public class ContextualWorkflowDriverImpl implements WorkflowDriver {
  private final static String LOG_PREFIX = "ContextualWorkflowDriverImpl: ";

  private RuntimeService runtimeService;
  private TaskService taskService;
  private FormService formService;
  private HistoryService historyService;
  private RepositoryService repositoryService;

  public static String getTitleToShow(@NonNull String workflowId) {
    return "title";// CacheHelper.getWorkflowDescription(workflowId);
  }

  public ContextualWorkflowDriverImpl(RuntimeService runtimeService, TaskService taskService, FormService formService,
                                      HistoryService historyService, RepositoryService repositoryService) {
    this.runtimeService = runtimeService;
    this.taskService = taskService;
    this.formService = formService;
    this.historyService = historyService;
    this.repositoryService = repositoryService;
  }

  @Override
  public WorkflowExecutionResponse startWorkflow
    (String tenantId, String userId, String processName, Map<String, Object> input) {
    log.info(LOG_PREFIX + "Start Process: {}", processName);
    ProcessInstance processInstance = null;
    HashMap<String, Object> args = new HashMap<>();
    args.putAll(input);
    args.put(WorkflowConstants.WORKFLOW_NAME, processName);
    args.put(WorkflowConstants.USER_ID, userId);
    try {
      processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processName, args, tenantId);
      log.info(LOG_PREFIX + "started Process: {} description: {}", processInstance.getName(), processInstance.getDescription());
    } catch (ActivitiObjectNotFoundException aonfx) {
      log.error("Error starting workflow", aonfx);
      return WorkflowExecutionResponse.builder().status(500).statusDetail(aonfx.getMessage())
        .tenantId(tenantId).processName(processName).build();
    }

    Optional<Task> taskOptional = getTask(processInstance);
    if (taskOptional.isPresent()) {
      return buildWorkflowExecutionResponse(tenantId, taskOptional.get(), processInstance, processName);
    } else {
      return WorkflowExecutionResponse.builder().tenantId(tenantId).processCompleted(true)
        .processInstanceId(processInstance.getId()).processInstanceName(processName).build();
    }
  }

  @Override
  public WorkflowExecutionResponse executeUserTask(String tenantId, String processInstanceId, Map<String, Object> input) {
    log.info(LOG_PREFIX + "Execute Process: {}", processInstanceId);
    Optional<ProcessInstance> optionalProcessInstance = getProcessInstance(tenantId, processInstanceId);
    if (!optionalProcessInstance.isPresent())
      return WorkflowExecutionResponse.builder().tenantId(tenantId).processInstanceId(processInstanceId)
        .status(404).statusDetail("Process not found").build();

    Optional<Execution> optionalExecution = getExecution(processInstanceId);
    if (!optionalExecution.isPresent())
      return WorkflowExecutionResponse.builder().tenantId(tenantId).processInstanceId(processInstanceId)
        .status(404).statusDetail("Execution not found").build();

    Optional<Task> optionalTask = getTask(optionalProcessInstance.get());
    if (!optionalTask.isPresent())
      return WorkflowExecutionResponse.builder().tenantId(tenantId).
        processInstanceId(processInstanceId).status(400).statusDetail("No Task Found at this stage").build();

    // Completed
    if (isProcessCompleted(optionalTask.get())) {
      log.info(LOG_PREFIX + " Workflow: {} Already Completed.", processInstanceId);
      return WorkflowExecutionResponse.builder().
        tenantId(tenantId).
        processInstanceId(processInstanceId).
        processCompleted(Boolean.TRUE).
        statusDetail("Workflow Completed.").
        build();
    }

    // Invoke the Task. Move on in the workflow
    taskService.complete(optionalTask.get().getId(), input);
    log.info(LOG_PREFIX + "ProcessInstanceId: {} Completed Task: {}", processInstanceId, optionalTask.get().getId());

    // Next task
    Optional<Task> optionalNextTask = getTask(optionalProcessInstance.get());
    if (!optionalNextTask.isPresent()) {
      return WorkflowExecutionResponse.builder().
        tenantId(tenantId).
        processInstanceId(processInstanceId).
        processCompleted(Boolean.TRUE).
        statusDetail("Workflow Completed.").
        build();
    } else {
      return WorkflowExecutionResponse.builder().
        tenantId(tenantId).
        processInstanceId(processInstanceId).
        processInstanceName(optionalProcessInstance.get().getName()).
        taskInputParams(getRequiredInputParams(optionalNextTask.get(), processInstanceId)).
        statusDetail("Workflow continuing.").
        build();
    }
  }

  @Override
  public WorkflowExecutionResponse status(String tenantId, String processInstanceId) {
    log.info(LOG_PREFIX + "Status Process: {}", processInstanceId);

    List<HistoricTaskInstance> taskList = historyService
      .createHistoricTaskInstanceQuery()
      .processInstanceId(processInstanceId)
      .list();
    StringBuilder sb = new StringBuilder();
    taskList.stream().forEach(historicTaskInstance -> {
      sb.append(new Gson().toJson(historicTaskInstance));
      sb.append("\r\n");
    });

    WorkflowExecutionResponse response = WorkflowExecutionResponse.builder().tenantId(tenantId)
      .processInstanceId(processInstanceId).statusDetail(sb.toString()).build();
    log.info("WorkflowExecution processInstanceId: {} response: {}", processInstanceId, response.json());

    return response;
  }

  @Override
  public WorkflowExecutionResponse exitWorkflow(String tenantId, String processInstanceId) {
    log.info(LOG_PREFIX + "Exit Process: {}", processInstanceId);

    // Get current task, and pass it inputs to resume workflow
    Optional<ProcessInstance> processInstance = getProcessInstance(tenantId, processInstanceId);
    if (processInstance.isPresent()) {
      Optional<Task> task = getTask(processInstance.get());
      if (task.isPresent())
        taskService.complete(task.get().getId());
    }

    return WorkflowExecutionResponse.builder().
      tenantId(tenantId).
      processInstanceId(processInstanceId).
      processCompleted(true).
      processCompletionMessage("Task completed").
      build();
  }

  private WorkflowExecutionResponse buildWorkflowExecutionResponse
    (String tenantId, @NonNull Task task,
     @NonNull ProcessInstance processInstance,
     @NonNull String processName) {
    WorkflowExecutionResponse response = WorkflowExecutionResponse.builder().
      tenantId(tenantId).
      processName(processName).
      processInstanceId(processInstance.getId()).
      processInstanceName(processInstance.getName()).
      statusDetail(task.getDescription()).
      processInstanceMessage(task.getDescription()).
      processCompleted(isProcessCompleted(task)).
      taskInputParams(getRequiredInputParams(task, processInstance.getId())).
      build();

    return response;
  }

  private Boolean userInputError(Execution execution) {
    // User Input Error
    Map<String, Object> variables = runtimeService.getVariables(execution.getId());
    if (MapUtils.isNotEmpty(variables) && variables.containsKey(USER_INPUT_VALID)) {
      if (Boolean.FALSE == variables.get(USER_INPUT_VALID)) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  private Optional<Execution> getExecution(@NonNull String processInstanceId) {
    List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
    if (executions == null || CollectionUtils.isEmpty(executions)) {
      return Optional.empty();
    }
    return Optional.of(executions.get(0));
  }

  /**
   * Get 1st process Instance if found
   *
   * @param tenantId
   * @param processInstanceId
   * @return ProcessInstance
   */
  private Optional<ProcessInstance> getProcessInstance(@NonNull String tenantId, @NonNull String processInstanceId) {
    List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).list();
    if (processInstances == null || CollectionUtils.isEmpty(processInstances)) {
      return Optional.empty();
    } else {
      return Optional.of(processInstances.get(0));
    }
  }

  /**
   * Get the next set of Input Parameters if required.
   *
   * @param processInstanceId
   * @return Next set of User Inputs requried
   */
  private TaskInputParams getRequiredInputParams(@NonNull Task task, @NonNull String processInstanceId) {
    TaskInputParams params = new TaskInputParams();

    // any inputs required?
    TaskFormData taskFormData = formService.getTaskFormData(task.getId());
    if (taskFormData == null || CollectionUtils.isEmpty(taskFormData.getFormProperties())) {
      // no inputs required;
      return params;
    }

    // Get current execution variables
    List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
    if (CollectionUtils.isEmpty(executions))
      return params;

    String executionId = executions.get(0).getId();
    Map<String, Object> executionData = runtimeService.getVariables(executionId);

    List<FormProperty> formProperties = taskFormData.getFormProperties();
    List<FormProperty> requiredFormProperties = formProperties.stream().filter(formProperty -> executionData.get(formProperty.getId()) == null).collect(Collectors.toList());

    requiredFormProperties.stream().forEach(formProperty -> {
      StringBuilder sb = new StringBuilder().append(formProperty.getName()).append(" ").append(formProperty.getType()).append(" ").append(formProperty.getId()).append("\r\n");
      log.info(LOG_PREFIX + "ProcessInstance: {} Next Input Params: {}", processInstanceId, sb.toString());
      params.getInputParamList().add(TaskInputParam.builder().id(
        formProperty.
          getId()).
        name(formProperty.getName()).
        value(formProperty.getValue()).
        sensitive((formProperty.getId().contains("Password") || formProperty.getId().contains("password") || formProperty.getName().contains("Password") || formProperty.getName().contains("password")) ? Boolean.TRUE : Boolean.FALSE).
        required(formProperty.isRequired()).build());
    });

    return params;
  }

  private Boolean isProcessCompleted(@NonNull Task task) {
    // If the process has ended?
    List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
    if (CollectionUtils.isEmpty(processInstances) || processInstances.get(0).isEnded()) {
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }

  private Optional<Task> getTask(@NonNull ProcessInstance processInstance) {
    // Get next task, if available
    Task task = taskService.createTaskQuery()
      .processInstanceId(processInstance.getId())
      .singleResult();

    return Optional.ofNullable(task);
  }
}
