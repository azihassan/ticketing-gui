package io.hahn.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets Category
 */

public enum Category {
  
  NETWORK("NETWORK"),
  
  HARDWARE("HARDWARE"),
  
  SOFTWARE("SOFTWARE"),
  
  OTHER("OTHER");

  private String value;

  Category(String value) {
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
  public static Category fromValue(String value) {
    for (Category b : Category.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

