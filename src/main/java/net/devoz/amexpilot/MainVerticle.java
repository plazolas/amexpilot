package net.devoz.amexpilot;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import net.devoz.amexpilot.app.controller.UserController;
import net.devoz.amexpilot.app.config.PropertiesLoader;

import java.util.Properties;

public class MainVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    UserController userController = new UserController(vertx);
    Router router = userController.getRouter();
    Properties prop = PropertiesLoader.loadProperties();

    int severPort = Integer.parseInt(prop.getProperty("server.port"));

    // Create the HTTP server
    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(severPort)
      .onSuccess(server -> {
        System.out.println("HTTP server started on port " + server.actualPort());
      })
      .onFailure(Throwable::printStackTrace);
  }

  public static void main(String[] args){
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}


