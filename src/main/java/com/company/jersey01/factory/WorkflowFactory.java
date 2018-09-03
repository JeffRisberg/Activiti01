package com.company.jersey01.factory;

import com.aisera.connector.beans.workflow.WorkflowDefinition;
import com.aisera.connector.framework.core1.controllers.WorkflowController;
import com.aisera.conversation.workflow.ContextualWorkflowDriverImpl;
import com.aisera.conversation.workflow.WorkflowDriver;
import com.aisera.conversation.workflow.impl.WorkflowDriverImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;

/**
 * Factory to get Process Engine and other Workflow Runtime Services
 */
@Slf4j
public class WorkflowFactory {

  private static ProcessEngine processEngine;
  private static RepositoryService repositoryService;
  private static RuntimeService runtimeService;
  private static TaskService taskService;
  private static FormService formService;
  private static HistoryService historyService;

  // Workflow Driver
  private static WorkflowDriver workflowDriver;

   // Workflow Controller
  private static WorkflowController workflowController;

  static {
    log.info("Initializing ProcessEngine Creation...");
    processEngine = ProcessEngines.getDefaultProcessEngine();
    repositoryService = processEngine.getRepositoryService();
    runtimeService = processEngine.getRuntimeService();
    taskService = processEngine.getTaskService();
    formService = processEngine.getFormService();
    historyService = processEngine.getHistoryService();
    log.info("Process Engine Initialized.");

    // Create workflow driver
    log.info("Building WorkflowDriver...");
     workflowDriver = new ContextualWorkflowDriverImpl(runtimeService, taskService, formService, historyService, repositoryService);
//    workflowDriver = new WorkflowDriverImpl(runtimeService, taskService, formService, historyService, repositoryService);
    log.info("WorkflowDriver initialized.");
  }

  public static WorkflowController getWorkflowController() {
    if(workflowController == null)
      workflowController = new WorkflowController();

    return workflowController;
  }

  /**
   * Delete a deployment.
   * @param deploymentId
   */
  public static void deleteDeployment(@NonNull String deploymentId) {
    try {
      log.info("Deleting Deployment: {}", deploymentId);
      repositoryService.deleteDeployment(deploymentId, true);
      log.info("Deployment successfully deleted.");
    } catch(Exception x) {
      log.error("Error deleting deployment.", x);
    }
  }

  public static void addDeployment(@NonNull String tenantId, @NonNull String processDefinitionKey) {
    // Lookup the definition
    WorkflowDefinition workflowDefinition = getWorkflowController().getWorkflowDefinition(processDefinitionKey);
    if(workflowDefinition == null) {
      log.error("Workflow Definition not available: {}. Workflow not deployed.", processDefinitionKey);
      return;
    }

    log.info("Adding Process Definition: {} for tenant: {}", workflowDefinition.getDefinition(), tenantId);
    repositoryService.createDeployment().addClasspathResource(workflowDefinition.getDefinition()).tenantId(tenantId).deploy();
    log.info("Process Definition: {} successfully deployed for tenant: {}", workflowDefinition.getDefinition(), tenantId);
  }

  public static ProcessEngine getProcessEngine() {
    return processEngine;
  }

  public static RepositoryService getRepositoryService() {
    return repositoryService;
  }

  public static RuntimeService getRuntimeService() {
    return runtimeService;
  }

  public static TaskService getTaskService() {
    return taskService;
  }

  public static FormService getFormService() {
    return formService;
  }

  public static HistoryService getHistoryService() {
    return historyService;
  }

  public static WorkflowDriver getWorkflowDriver() {
    return workflowDriver;
  }
}
