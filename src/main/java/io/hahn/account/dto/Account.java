package io.hahn.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Account
 */

public class Account {

  private Long id = null;

  private String username;

  private List<Role> roles = new ArrayList<>();

  public Account id(Long id) {
    this.id = id;
    return this;
  }

  public boolean hasRole(String role) {
    return roles.stream().map(Role::getRole).anyMatch(r -> r.equals(role));
  }

  /**
   * Get id
   * @return id
   */

  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Account username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
   */

  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Account roles(List<Role> roles) {
    this.roles = roles;
    return this;
  }

  public Account addRolesItem(Role rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Get roles
   * @return roles
   */
  @JsonProperty("roles")
  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account account = (Account) o;
    return Objects.equals(this.id, account.id) &&
            Objects.equals(this.username, account.username) &&
            Objects.equals(this.roles, account.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Account {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
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

