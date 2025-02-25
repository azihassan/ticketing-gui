package io.hahn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * CommentCreate
 */

public class CommentCreate {

  private String text;

  public CommentCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CommentCreate(String text) {
    this.text = text;
  }

  public CommentCreate text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
   */
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentCreate commentCreate = (CommentCreate) o;
    return Objects.equals(this.text, commentCreate.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommentCreate {\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

