package net.devoz.amexpilot.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.devoz.amexpilot.MainVerticle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.devoz.amexpilot.app.user.User;

@ExtendWith(VertxExtension.class)
public class UserServiceTest {

  private String jsonData = "";

  @Test
  void serverIsActiveTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      client.request(HttpMethod.GET, 8888, "localhost", "/")
        .compose(req -> req.send().compose(HttpClientResponse::body))
        .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
          Assertions.assertTrue((buffer.toString()).contains("active"));
          testContext.completeNow();
        })));
    }));
  }

  @Test
  void insertUserSuccessTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      User user = new User(0, "don", "don@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.POST,8888, "localhost", "/users")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .putHeader("content-length", String.valueOf(jsonData.length()))
          .send(jsonData)
        .onComplete(testContext.succeeding(response -> {
          Assertions.assertEquals(201, response.statusCode());
          Assertions.assertNotNull(response.body());
          response.bodyHandler(body -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
              User newUser = objectMapper.readValue(body.toString(), User.class);
              Assertions.assertTrue(newUser.id() > 0);
              Assertions.assertTrue(newUser.name().contains(user.name()));
              Assertions.assertTrue(newUser.email().contains(user.email()));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
          testContext.completeNow();
        })));
    }));
  }

  @Test
  void insertUserFailTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      User user = new User(1, "don", "don@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.POST, 8888, "localhost", "/users")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .putHeader("content-length", String.valueOf(jsonData.length()))
          .send(jsonData)
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(400, response.statusCode());
            Assertions.assertNotNull(response.body());
            response.bodyHandler(buffer -> Assertions.assertTrue(buffer.toString().contains("User ID should be empty.")));
            testContext.completeNow();
          })));
    }));
  }

  @Test
  void findAllUsersSuccessTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      User user = new User(0, "ron", "ron@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.POST, 8888, "localhost", "/users")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .putHeader("content-length", String.valueOf(jsonData.length()))
          .send(jsonData)
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(201, response.statusCode());
            testContext.completeNow();
          })));

      testContext.completeNow();
      client.request(HttpMethod.GET, 8888, "localhost", "/users")
        .compose(req -> req
          .send()
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
              Assertions.assertTrue(buffer.toString().contains("ron@mail.com"));
              testContext.completeNow();
            });
          })));

    }));
  }

  @Test
  void findOneUserSuccessTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      // Insert user
      User user = new User(0, "ron", "ron@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.POST, 8888, "localhost", "/users")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .putHeader("content-length", String.valueOf(jsonData.length()))
          .send(jsonData)
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(201, response.statusCode());
            testContext.completeNow();
          })));
      client.request(HttpMethod.GET, 8888, "localhost", "/users/1")
        .compose(req -> req
          .send()
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> Assertions.assertTrue(buffer.toString().contains("ron@mail.com")));
            testContext.completeNow();
          })));
    }));
  }

  @Test
  void findOneUserFailTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      // Insert user
      User user = new User(0, "ron", "ron@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.POST, 8888, "localhost", "/users")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .putHeader("content-length", String.valueOf(jsonData.length()))
          .send(jsonData)
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(201, response.statusCode());
            testContext.completeNow();
          })));
      client.request(HttpMethod.GET, 8888, "localhost", "/users/2")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .send()
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(404, response.statusCode());
            response.bodyHandler(buffer -> Assertions.assertTrue(buffer.toString().contains("User with id 2 not found")));
            testContext.completeNow();
          })));
    }));
  }

  @Test
  void deleteUserSuccessTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      // Insert user
      User user = new User(0, "ron", "ron@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.POST, 8888, "localhost", "/users")
        .compose(req -> req
          .putHeader("content-type", "application/json")
          .putHeader("content-length", String.valueOf(jsonData.length()))
          .send(jsonData)
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(201, response.statusCode());
            testContext.completeNow();
          })));
      client.request(HttpMethod.DELETE, 8888, "localhost", "/users/1")
        .compose(req -> req
          .send()
          .onComplete(testContext.succeeding(response -> {
            Assertions.assertEquals(200, response.statusCode());
            testContext.completeNow();
          })));
    }));
  }

  @Test
  void updateUserFailTest(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> {
      HttpClient client = vertx.createHttpClient();
      User user = new User(1, "ronald", "ronald@mail.com");
      jsonData = user.toJson();
      client.request(HttpMethod.PUT, 8888, "localhost", "/users")
        .compose(request -> request
          .putHeader("content-type", "application/json")
          .send(jsonData)
          .onComplete(testContext.succeeding(resp -> {
            Assertions.assertEquals(404, resp.statusCode());
            resp.bodyHandler(buffer -> Assertions.assertTrue(buffer.toString().contains("ronald@mail.com")));
            testContext.completeNow();
          })));

    }));
  }

}
