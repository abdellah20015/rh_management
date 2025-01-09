package com.example.rh;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.openapi.contract.OpenAPIContract;

public class MainVerticle extends AbstractVerticle {
  private static final Integer PORT = 8888;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String path = "src/main/api/openapi.json";
    OpenAPIContract.from(vertx, path)
    .onSuccess(contract ->{
      // Create a router builder
      RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract);
      // Create a session store
      SessionStore sessionStore = LocalSessionStore.create(vertx);
      // Create a session handler
      SessionHandler sessionHandler = SessionHandler.create(sessionStore);

      // Mount the session handler
      routerBuilder.rootHandler(sessionHandler);

      // Mount the CORS handler
      routerBuilder.rootHandler(
        CorsHandler.create()
          .addOrigin("*")
          .allowedMethod(HttpMethod.PUT)
          .allowedMethod(HttpMethod.DELETE)
          .allowedHeader("Authorization")
          .allowedHeader("Content-Type")
          .allowCredentials(true)
      );

      // Mount the body handler
      routerBuilder.rootHandler(BodyHandler.create().setBodyLimit(50 * 1024 * 1024));


      // Create a router
      Router router = routerBuilder.createRouter();

      //  create a static handler for the uploads directory 
      router.route("/uploads/*").handler(StaticHandler.create("uploads"));

      //  create http server and listen on port 8888  
      vertx.createHttpServer().requestHandler(router).listen(PORT)
      .onComplete(http ->{
        if(http.succeeded()){
          startPromise.complete();
          System.out.println("HTTP server started on port " + PORT);
        }else{
          startPromise.fail(http.cause());
        }
      });
    })
    .onFailure(err ->{
      startPromise.fail(err);
      System.err.println("Failed to load OpenAPI contract: " + err.getMessage());
    });
  }
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
