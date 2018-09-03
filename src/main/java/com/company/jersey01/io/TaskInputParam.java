package com.company.jersey01.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInputParam {
  /**
   * Id of Input
   */
  private String id;

  /**
   * Name
   */
  private String name;

  /**
   * Value (User Question) Text - which can be displayed in a Channel to ask for Input
   */
  private String value;

  /**
   * Is this Input required?
   */
  private Boolean required;

  /**
   * Should the display be masked? like passwords or ssn
   */
  private Boolean sensitive;
}
