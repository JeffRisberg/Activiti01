package com.company.jersey01.workflow.event;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

@Slf4j
public class EndEventListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        log.info("EndEventListener id: {} eventName: {} processDefinitionId: {} processInstanceId: {}",
                execution.getId(), execution.getEventName(), execution.getProcessDefinitionId(), execution.getProcessInstanceId());
    }
}
