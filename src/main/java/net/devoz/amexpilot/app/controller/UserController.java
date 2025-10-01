package net.devoz.amexpilot.app.controller;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import net.devoz.amexpilot.app.error.UserNotFoundException;
import net.devoz.amexpilot.app.service.UserService;
import net.devoz.amexpilot.app.user.InMemoryUserRepository;
import net.devoz.amexpilot.app.user.User;
import net.devoz.amexpilot.app.config.PropertiesLoader;
import net.devoz.amexpilot.app.user.dbUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class UserController {

  private static final Logger logger = Logger.getLogger(UserController.class.getName());
  private final Router router;
  private final UserService userService;

  public UserController(Vertx vertx) {
    this.router = Router.router(vertx);

    Properties properties = PropertiesLoader.loadProperties();
    String DEFAULT_ROUTER_PATH = properties.getProperty("router.path");
    String datasourceType = properties.getProperty("datasource.type");

    if(datasourceType.contains("inmemory")) {
      this.userService = new UserService(new InMemoryUserRepository());
    } else {
      this.userService = new UserService(new dbUserRepository());
    }

    // Set up the routes and their handlers
    router.get("/").handler(this::getStatus);
    router.get(DEFAULT_ROUTER_PATH).handler(this::getAll);
    router.get(DEFAULT_ROUTER_PATH + "/:id").handler(this::getOne);
    router.post(DEFAULT_ROUTER_PATH).handler(BodyHandler.create()).handler(this::addOne);
    router.put(DEFAULT_ROUTER_PATH).handler(BodyHandler.create()).handler(this::updateOne);
    router.delete(DEFAULT_ROUTER_PATH + "/:id").handler(this::deleteOne);
  }

  public Router getRouter() {
    return router;
  }

  private void getStatus(RoutingContext rc) {
    rc.response().setStatusCode(200).end("active");
  }

  // Handler to get all users
  private void getAll(RoutingContext rc) {
    List<User> users = this.userService.findAll();
    JsonArray jsonArray = new JsonArray();
    for (User user : users) {
      JsonObject jsonObject = User.toJsonObject(user);
      jsonArray.add(jsonObject);
    }
    rc.response()
      .putHeader("content-type", "application/json")
      .end(jsonArray.encodePrettily());
  }

  // Handler to get a single user by ID
  private void getOne(RoutingContext rc) {
    try {
      Optional<User> user = this.userService.findById(Long.parseLong(rc.pathParam("id")));
      if(user.isPresent()) {
        JsonObject jsonObject = User.toJsonObject(user.get());
        rc.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(Json.encodePrettily(jsonObject));
      } else {
        throw new UserNotFoundException("No User found with id " + rc.pathParam("id"));
      }

    } catch (UserNotFoundException e) {
      rc.response().setStatusCode(404).end(e.getMessage());
    } catch (NumberFormatException e) {
      logger.warning(e.getMessage());
      rc.response().setStatusCode(400).end("Bad Request. NumberFormatException. " + e.getMessage());
    } catch (Exception e) {
      logger.warning(e.getMessage());
      rc.response().setStatusCode(500).end(e.getMessage());
    }
  }

  // Handler to add a new user
  private void addOne(RoutingContext rc) {

    try {
      JsonObject jsonObject = rc.body().asJsonObject();
      if(jsonObject != null) {
        String id = jsonObject.getString("id");
        if(id != null && !id.equals("0")) {
          rc.response().setStatusCode(400).end("User ID should be empty.");
          return;
        }
        User user = new User( 0L, jsonObject.getString("name"), jsonObject.getString("email"));
        User newUser = this.userService.save(user);
        rc.response()
          .setStatusCode(201)
          .putHeader("content-type", "application/json")
          .end(Json.encodePrettily(User.toJsonObject(newUser)));
      } else {
        throw new UserNotFoundException("No Request Body found.");
      }
    } catch (UserNotFoundException e) {
      rc.response().setStatusCode(404).end(e.getMessage());
    } catch (NumberFormatException e) {
      logger.warning(e.getMessage());
      rc.response().setStatusCode(400).end("Bad Request. " + e.getMessage());
    } catch (Exception e) {
      logger.warning(e.getMessage());
      rc.response().setStatusCode(500).end(e.getMessage());
    }
  }

  // Handler to update user
  private void updateOne(RoutingContext rc) {

    try {
      JsonObject jsonObject = rc.body().asJsonObject();
      if(jsonObject != null) {
        User user = new User( Long.parseLong(jsonObject.getString("id")),
                              jsonObject.getString("name"),
                              jsonObject.getString("email"));
        this.userService.update(user).ifPresent(value -> rc.response()
          .setStatusCode(201)
          .putHeader("content-type", "application/json")
          .end(Json.encodePrettily(User.toJsonObject(value))));
      } else {
        throw new UserNotFoundException("No Request Body found.");
      }
    } catch (UserNotFoundException e) {
      rc.response().setStatusCode(404).end("User no found. " + e.getMessage());
    } catch (NumberFormatException e) {
      logger.warning(e.getMessage());
      rc.response().setStatusCode(400).end("Bad Request. " + e.getMessage());
    } catch (Exception e) {
      logger.warning(e.getMessage());
      rc.response().setStatusCode(500).end(e.getMessage());
    }
  }

  // Handler to delete a user by ID
  private void deleteOne(RoutingContext rc) {
      long id = Long.parseLong(rc.pathParam("id"));
      if(id > 0) {
        this.userService.delete(id);
      }
      rc.response().setStatusCode(200).end("User deleted");
  }

}
