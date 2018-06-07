package com.company.jersey01.endpoints;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("processes")
public class ActivitiEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiEndpoint.class);

    private RuntimeService runtimeService;
    private TaskService taskService;

    @Inject
    public ActivitiEndpoint(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Path("/start-process")
    @GET
    public String startProcess() {
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("numberOfDays", new Long(5));
        variables.put("startDate", new Date(200, 1, 1));
        variables.put("reason", "vacationLeave");

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("vacationRequest", variables);

        System.out.println(processInstance.getId());
        return "Process started. Number of currently running process instances = " + runtimeService.createProcessInstanceQuery()
                .count();
    }

    @Path("/get-tasks/{pid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TaskRepresentation> getTasks(@PathParam("pid") String processInstanceId) {
        List<Task> usertasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        return usertasks.stream()
                .map(task -> new TaskRepresentation(task.getId(), task.getName(), task.getProcessInstanceId()))
                .collect(Collectors.toList());
    }

    @Path("/complete-task-A/{processInstanceId}")
    @GET
    public void completeTaskA(@PathParam("processInstanceId") String processInstanceId) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("vacationApproved", Boolean.TRUE);
        variables.put("comments", "have a great time");

        taskService.complete(task.getId(), variables);
        logger.info("Task completed");
    }
}
