package com.example.rh;



import com.example.rh.constants.Collections;
import com.example.rh.constants.Services;
import com.example.rh.services.AuthVerticle;
import com.example.rh.services.Db;

import com.example.rh.constants.Services;
import com.example.rh.services.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.router.RequestExtractor;
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
      RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract, RequestExtractor.withBodyHandler());
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

      //demands
      routerBuilder.getRoute("listDemands").addHandler(this::getListDemands);
      routerBuilder.getRoute("createDemand").addHandler(this::createDemand);
      routerBuilder.getRoute("updateDemand").addHandler(this::updateDemand);
      routerBuilder.getRoute("getNotifications").addHandler(this::getNotification);

      // Add handlers

      // path: /login
      routerBuilder.getRoute("login").addHandler(this::LoginHandler);

      // path: /private/logout
      routerBuilder.getRoute("logout").addHandler(this::LogoutHandler);

      // path: /private/resetPassword
      routerBuilder.getRoute("resetPassword").addHandler(this::resetPasswordHandler);

      //  path : /private/user/list
      routerBuilder.getRoute("listUsers").addHandler(this::ListUsersHandler);

      // path : /private/user/create
      routerBuilder.getRoute("createUser").addHandler(this::createUserHandler);

      // path : /private/user/update
      routerBuilder.getRoute("updateUser").addHandler(this::updateUserHandler);

      // path : /private/user/delete
      routerBuilder.getRoute("deleteUser").addHandler(this::DeleteUserHandler);

      // path : /private/user/profile
      routerBuilder.getRoute("getUserProfile").addHandler(this::getUserProfileHandler);

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


  /**
   * @param ctx RoutingContext
   * @author Youssef
   * <p>
   * OpenAPI3 Route getListDemands
   * request body <JsonObject>
   * </p>
   */
  private void getListDemands(RoutingContext ctx) {
    try {
      JsonObject body = ctx.getBodyAsJson();
//      body.put("user_role", ctx.user().get("role"));
//      body.put("user_id", ctx.user().get("_id"));

      vertx.eventBus().request(Services.DEMAND_LIST ,body , res-> {
        if(res.succeeded()){
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(res.result().body().toString());
        }else {
          ctx.response()
            .setStatusCode(500)
            .putHeader("content-type", "application/json")
            .end(res.cause().getMessage());
        }
      });

    }catch(Exception e) {
      System.out.println("error " + e);
    }
  }

  /**
   * @param ctx RoutingContext
   * @author Youssef
   * <p>
   * OpenAPI3 Route createDemand
   * request body <JsonObject>
   * </p>
   */
  public void createDemand(RoutingContext ctx) {
    try {
      JsonObject body = ctx.getBodyAsJson();

      vertx.eventBus().request(Services.DEMAND_CREATE, body, res -> {
        if(res.succeeded()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(res.result().body().toString());
        }else {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(res.cause().getMessage());
        }
      });
    }catch(Exception e) {
      System.out.println("error " + e);
    }
  }

  /**
   * @param ctx RoutingContext
   * @author Youssef
   * <p>
   * OpenAPI3 Route updateDomand
   * request body <JsonObject>
   * </p>
   */
  public void updateDemand(RoutingContext ctx) {
    try{
      JsonObject body = ctx.getBodyAsJson();

      vertx.eventBus().request(Services.DEMAND_UPDATE, body, res -> {
        if(res.succeeded()) {
          ctx.response()
            .putHeader("content-type" , "application/json")
            .end(res.result().body().toString());
        }else {
          ctx.response()
            .putHeader("content-type" , "application/json")
            .end(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      System.out.println("error " + e);
    }
  }

  /**
   * @param ctx RoutingContext
   * @author Youssef
   * <p>
   * OpenAPI3 Route getNotifications
   * request body <JsonObject>
   * </p>
   */
  public void getNotification(RoutingContext ctx) {
    JsonObject body = ctx.getBodyAsJson();

    try {
      vertx.eventBus().request(Services.NOTIFICATION_LIST, body, res -> {
        if (res.succeeded()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(res.result().body().toString());
        } else {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(res.cause().getMessage());
        }
      });
    } catch (Exception e) {
      System.out.println("error " + e);
    }
  }



 /**
  * Login handler
  * @author Ilyass
    login method to authenticate the user and create a session for him
  */
  public void LoginHandler(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    vertx.eventBus().request(Services.AUTH_LOGIN, body , reply ->{
      if(reply.succeeded() && reply.cause() == null){
        JsonObject response = (JsonObject) reply.result().body();
        User user = User.create(response.getJsonObject("user"));
        ctx.setUser(user);
        ctx.session().regenerateId();
        System.out.println(ctx.user().principal());
        ctx.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(response.encode());
      }else{
        ctx.response().setStatusCode(401).end(reply.cause().getMessage());
      }
    });
  }
  /**
   * Logout handler
   * @author ilyass
   * logout method to destroy the session of the user
   */
  public void LogoutHandler(RoutingContext ctx) {
    ctx.clearUser();
    ctx.session().destroy();
    ctx.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(new JsonObject().put("message", "logout successful").encode());
  }
  /**
    * Reset password handler
    * @author ilyass
    * reset password method to reset the password of the user
   */
  public void resetPasswordHandler(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    body.put("username", ctx.user().principal().getString("username"));
    vertx.eventBus().request(Services.AUTH_RESET_PASSWORD, body, reply -> {
      if (reply.succeeded()) {
        ctx.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", "Password reset successfully").encode());
      } else {
        ctx.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", reply.cause().getMessage()).encode());
      }
    });
  }

  /**
   * List users handler
   * @param ctx RoutingContext
   * @author ilyass
   * list users method to list all the users with aggregation
   */
  public void ListUsersHandler(RoutingContext ctx) {
    try {
      JsonObject body = ctx.body().asJsonObject();
      JsonObject option = body.getJsonObject("query");
      int page = option.getInteger("page");
      int limit = option.getInteger("limit");
      int skip = (page - 1) * limit;

      JsonObject match = new JsonObject();
      if (!ctx.user().principal().getString("role").equals("admin")) {
        match.put("_id", ctx.user().principal().getString("id"));
      }

      JsonObject lookupContracts = new JsonObject()
          .put("from", Collections.CONTRACTS)
          .put("localField", "_id")
          .put("foreignField", "user_id")
          .put("as", "contracts");

      JsonObject lookupDemands = new JsonObject()
          .put("from", Collections.DEMANDS)
          .put("localField", "_id")
          .put("foreignField", "user_id")
          .put("as", "demands");

      JsonObject aggregate = new JsonObject()
          .put("collection", Collections.USER)
          .put("pipeline", new JsonArray()
              .add(new JsonObject().put("$match", match))
              .add(new JsonObject().put("$lookup", lookupContracts))
              .add(new JsonObject().put("$lookup", lookupDemands))
              .add(new JsonObject().put("$skip", skip))
              .add(new JsonObject().put("$limit", limit))
          )
          .put("options", new JsonObject());

      vertx.eventBus().request(Services.DB_AGGREGATE, aggregate, reply -> {
        if (reply.succeeded()) {
          ctx.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(reply.result().body().toString());
        } else {
          ctx.response()
            .setStatusCode(500)
            .putHeader("content-type", "application/json")
            .end(new JsonObject().put("message", reply.cause().getMessage()).encode());
        }
      });
    } catch (Exception e) {
      ctx.response()
        .setStatusCode(500)
        .putHeader("content-type", "application/json")
        .end(new JsonObject().put("message", "Internal server error: " + e.getMessage()).encode());
    }
  }

  /**
   * Create user handler
   * @author ilyass
   * create user method to create a new user
   */
  public void createUserHandler(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    vertx.eventBus().request(Services.USER_CREATE, body , reply ->{
      if(reply.succeeded() && reply.cause() == null){
        JsonObject response = (JsonObject) reply.result().body();
        ctx.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(response.encode());
      }else{
        ctx.response()
        .setStatusCode(409)
        .putHeader("content-type", "application/json")
        .end(new JsonObject().put("message", reply.cause().getMessage()).encode());
      }
    });
  }
  /**
   * Update user handler
   * @author ilyass
   * update user method to update the user information
   */
  public void updateUserHandler(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    String userId = body.getString("user_id");
    JsonObject update = body.getJsonObject("update");

    JsonObject payload = new JsonObject()
        .put("collection", Collections.USER)
        .put("id", userId)
        .put("update", update);

    vertx.eventBus().request(Services.DB_UPDATE, payload, reply -> {
      if (reply.succeeded()) {
        ctx.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", "User updated successfully").encode());
      } else {
        ctx.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", reply.cause().getMessage()).encode());
      }
    });
  }
  /**
   * Delete user handler
   * @author ilyass
   * delete user method to delete the user
   */
  public void DeleteUserHandler(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    String userId = body.getString("user_id");

    JsonObject payload = new JsonObject()
        .put("collection", Collections.USER)
        .put("id", userId);

    vertx.eventBus().request(Services.DB_REMOVE_DOCUMENT, payload, reply -> {
      if (reply.succeeded()) {
        ctx.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", "User deleted successfully").encode());
      } else {
        ctx.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", reply.cause().getMessage()).encode());
      }
    });
  }


  /**
   * Get user profile handler
   * @author ilyass
   * get user profile method to get the profile of the user
   */
  public void getUserProfileHandler(RoutingContext ctx) {
    String userId = ctx.user().principal().getString("id");

    JsonObject match = new JsonObject().put("_id", userId);

    JsonObject lookupContracts = new JsonObject()
        .put("from", Collections.CONTRACTS)
        .put("localField", "_id")
        .put("foreignField", "user_id")
        .put("as", "contracts");

    JsonObject lookupDemands = new JsonObject()
        .put("from", Collections.DEMANDS)
        .put("localField", "_id")
        .put("foreignField", "user_id")
        .put("as", "demands");

    JsonObject aggregate = new JsonObject()
        .put("collection", Collections.USER)
        .put("pipeline", new JsonArray()
            .add(new JsonObject().put("$match", match))
            .add(new JsonObject().put("$lookup", lookupContracts))
            .add(new JsonObject().put("$lookup", lookupDemands))
        )
        .put("options", new JsonObject());

    vertx.eventBus().request(Services.DB_AGGREGATE, aggregate, reply -> {
        if (reply.succeeded()) {
            ctx.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(reply.result().body().toString());
        } else {
            ctx.response()
                .setStatusCode(500)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("message", reply.cause().getMessage()).encode());
        }
    });
}











  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
    vertx.deployVerticle(new Db());
    vertx.deployVerticle(new Demand());
    vertx.deployVerticle(new AuthVerticle());
    vertx.deployVerticle(new PdfGenerator());
    vertx.deployVerticle(new Notifications());
  }
}
