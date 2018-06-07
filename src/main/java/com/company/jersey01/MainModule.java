package com.company.jersey01;

import com.company.jersey01.services.CharityService;
import com.company.jersey01.services.DonorService;
import com.google.inject.servlet.ServletModule;
import org.activiti.engine.*;

public class MainModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(CharityService.class);
        bind(DonorService.class);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        System.out.println("processEngine " + processEngine);

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        bind(RepositoryService.class).toInstance(repositoryService);
        bind(RuntimeService.class).toInstance(runtimeService);
        bind(TaskService.class).toInstance(taskService);

        repositoryService.createDeployment()
                .addClasspathResource("test/vacationRequest.bpmn20.xml")
                .deploy();

        Long count = repositoryService.createProcessDefinitionQuery().count();
        System.out.println("definition count " + count);
    }
}