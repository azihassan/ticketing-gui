package io.hahn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CommentPage
 */

public class CommentPage {

  private Integer totalPages;

  private String totalElements;

  private List<Comment> content = new ArrayList<>();

  public CommentPage totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Get totalPages
   * @return totalPages
   */
  
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public CommentPage totalElements(String totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Get totalElements
   * @return totalElements
   */
  
  @JsonProperty("totalElements")
  public String getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(String totalElements) {
    this.totalElements = totalElements;
  }

  public CommentPage content(List<Comment> content) {
    this.content = content;
    return this;
  }

  public CommentPage addContentItem(Comment contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }
    this.content.add(contentItem);
    return this;
  }

  /**
   * Get content
   * @return content
   */
  @JsonProperty("content")
  public List<Comment> getContent() {
    return content;
  }

  public void setContent(List<Comment> content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentPage commentPage = (CommentPage) o;
    return Objects.equals(this.totalPages, commentPage.totalPages) &&
        Objects.equals(this.totalElements, commentPage.totalElements) &&
        Objects.equals(this.content, commentPage.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalPages, totalElements, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommentPage {\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

