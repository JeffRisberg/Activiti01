package com.company.jersey01.workflow.context;

/**
 * Enum for what attributes are tracked across workflow executions during a session. Transient - not saved across
 * sessions.
 */
public enum ContextAttributeKey {
  USER_ID,
  USER_EMAIL
}
