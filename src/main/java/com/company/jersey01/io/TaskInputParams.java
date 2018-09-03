package com.company.jersey01.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class TaskInputParams {

  public TaskInputParams() {
    inputParamList = new ArrayList<>();
  }

  /**
   * List of Inputs required to continue the workflow execution
   */
  @Builder.Default
  private List<TaskInputParam> inputParamList = new ArrayList<>();

  private TaskInputParam inputParam;

  /**
   * Only 1 Input Param is configured for each User Task (to make it Channel friendly)
   * @return
   */
  public Optional<TaskInputParam> getInputParam() {
    if(CollectionUtils.isEmpty(inputParamList))
      return Optional.empty();

    // Return 1st input
    return Optional.of(inputParamList.get(0));
  }
}
