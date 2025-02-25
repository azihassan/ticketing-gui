package io.hahn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * TicketCreate
 */

public class TicketCreate {

  private String title;

  private String description;

  private Priority priority;

  private Category category;

  private Status status;

  public TicketCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TicketCreate(String title, String description, Priority priority, Category category, Status status) {
    this.title = title;
    this.description = description;
    this.priority = priority;
    this.category = category;
    this.status = status;
  }

  public TicketCreate title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public TicketCreate description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TicketCreate priority(Priority priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Get priority
   * @return priority
   */
  @JsonProperty("priority")
  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public TicketCreate category(Category category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
   */
  @JsonProperty("category")
  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public TicketCreate status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @JsonProperty("status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TicketCreate ticketCreate = (TicketCreate) o;
    return Objects.equals(this.title, ticketCreate.title) &&
        Objects.equals(this.description, ticketCreate.description) &&
        Objects.equals(this.priority, ticketCreate.priority) &&
        Objects.equals(this.category, ticketCreate.category) &&
        Objects.equals(this.status, ticketCreate.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, priority, category, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TicketCreate {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

