package net.devoz.amexpilot.app.user;

import io.vertx.core.json.JsonObject;

public record User(long id, String name, String email) {

  public static JsonObject toJsonObject(User user) {
    return new JsonObject()
      .put("id", user.id())
      .put("name", user.name())
      .put("email", user.email());
  }

  public String toJson() {
    return "{" + "\"id\" :" + id + ", \"name\" : \"" + name + "\", \"email\" : \"" + email + "\" }";
  }

}
