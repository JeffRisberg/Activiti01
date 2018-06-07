package com.company.servicetasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class SendEmailServiceTask implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        //logic to send email confirmation
        System.out.println("Sending email now");
    }

}
