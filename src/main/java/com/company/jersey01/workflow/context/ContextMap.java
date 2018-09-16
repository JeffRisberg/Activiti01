package com.company.jersey01.workflow.context;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Transient ContextMap for a workflow. All the workflow data inputs (qualified) are stored here.
 * A user can provide multiple inputs during a Conversation session - these are maintained here and made
 * available to all workflow executions. The goal is to not ask User for same input again - across workflows.
 */
@Slf4j
public class ContextMap {

  private static ContextMap contextMap = null;

  private ContextMap() {}

  public static ContextMap getInstance() {
    if(contextMap == null)
      contextMap = new ContextMap();
    return contextMap;
  }

  // By Tenant Id
  private Map<String,
    // By User Id
    Map<String,
      Map<ContextAttributeKey, ContextAttributeData>>> attributeMapByTenant = new HashMap<>();

  public void add(@NonNull String tenantId, @NonNull String userId, ContextAttributeKey key, ContextDataType type, Object value) {
    log.info("ContextMap.add tenantId: {} userId: {} key: {} type: {} value: {}", tenantId, userId, key.name(), type.name(), value.toString());
    if(attributeMapByTenant.get(tenantId) == null)
      attributeMapByTenant.put(tenantId, new HashMap<>());
    if(attributeMapByTenant.get(tenantId).get(userId) == null)
      attributeMapByTenant.get(tenantId).put(userId, new HashMap<>());

    attributeMapByTenant.get(tenantId).get(userId).put(key, ContextAttributeData.builder().type(type).value(value).build());
  }

  public Optional<ContextAttributeData> get(@NonNull String tenantId, @NonNull String userId, ContextAttributeKey key) {
    if(attributeMapByTenant.get(tenantId) != null && attributeMapByTenant.get(tenantId).get(userId) != null)
      return Optional.of(attributeMapByTenant.get(tenantId).get(userId).get(key));
    return Optional.empty();
  }

  /**
   * Get all attributes for specified tenantId
   * @param tenantId
   * @return All Attributes
   */
  public Map<String, Object> getAll(@NonNull String tenantId, @NonNull String userId) {
    if(attributeMapByTenant.get(tenantId) == null || attributeMapByTenant.get(tenantId).get(userId) == null)
      return Collections.emptyMap();
    // Build a map to be injected into runtime execution
    AtomicReference<Map<String, Object>> params = new AtomicReference<>();
    attributeMapByTenant.get(tenantId).get(userId).keySet().forEach(key -> {
      if(params.get() == null)
        params.set(new HashMap<>());
      params.get().put(key.name(), attributeMapByTenant.get(tenantId).get(userId).get(key).getValue());
    });
    return params.get();
  }

  public void clear(@NonNull String tenantId) {
    attributeMapByTenant.put(tenantId, new HashMap<>());
  }

  public static void main(String[] args) {
    ContextMap.getInstance().add("10000", "tester", ContextAttributeKey.USER_ID, ContextDataType.CONTEXT_STRING, "diwakar@aisera.com");
    ContextMap.getInstance().add("10000", "tester", ContextAttributeKey.USER_EMAIL, ContextDataType.CONTEXT_STRING, "diwakar_email@aisera.com");

    Map<String, Object> input = ContextMap.getInstance().getAll("10000", "tester");
    System.out.println(input.toString());

  }
}
