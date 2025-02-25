package io.hahn.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets Priority
 */

public enum Priority {
  
  LOW("LOW"),
  
  MEDIUM("MEDIUM"),
  
  HIGH("HIGH");

  private String value;

  Priority(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Priority fromValue(String value) {
    for (Priority b : Priority.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

