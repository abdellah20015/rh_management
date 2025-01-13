package com.example.rh;






import java.util.List;


import com.example.rh.services.Conf;
import com.example.rh.services.Contract;
import com.example.rh.services.Db;
import com.example.rh.services.Demand;
import com.example.rh.services.File;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Services;
import com.example.rh.services.AuthVerticle;



import com.example.rh.services.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.FileUpload;

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
    Conf.createMongoClient(vertx);
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
      routerBuilder.rootHandler(BodyHandler.create().setUploadsDirectory("uploads").setBodyLimit(50 * 1024 * 1024));

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

      // Contracts
      routerBuilder.getRoute("createContract").addHandler(this::createContract);
      routerBuilder.getRoute("updateContract").addHandler(this::updateContract);
      routerBuilder.getRoute("getContract").addHandler(this::getContract);

      // Files
      routerBuilder.getRoute("uploadFile").addHandler(this::uploadFile);
      routerBuilder.getRoute("getfiles").addHandler(this::getFiles);




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
 * @author abdellah
 * <p>
 * OpenAPI3 Route createContract
 * request body <JsonObject>
 * </p>
 */
public void createContract(RoutingContext ctx) {
  try {
    JsonObject body = ctx.getBodyAsJson();

    vertx.eventBus().request(Services.CONTRACT_CREATE, body, res -> {
      if (res.succeeded()) {
        ctx.response()
          .putHeader("content-type", "application/json")
          .end(res.result().body().toString());
      } else {
        ctx.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(res.cause().getMessage().contains("Champs requis manquants") ? 400 : 500)
          .end(new JsonObject()
            .put("error", res.cause().getMessage())
            .toString());
      }
    });
  } catch (Exception e) {
    System.out.println("error " + e);
    ctx.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(500)
      .end(new JsonObject()
        .put("error", "Erreur inattendue")
        .toString());
  }
}

/**
 * @param ctx RoutingContext
 * @author abdellah
 * <p>
 * OpenAPI3 Route updateContract
 * request body <JsonObject>
 * </p>
 */
public void updateContract(RoutingContext ctx) {
  try {
    JsonObject body = ctx.getBodyAsJson();

    vertx.eventBus().request(Services.CONTRACT_UPDATE, body, res -> {
      if (res.succeeded()) {
        ctx.response()
          .putHeader("content-type", "application/json")
          .end(res.result().body().toString());
      } else {
        ctx.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(res.cause().getMessage().contains("ID du contrat requis") ? 400 : 500)
          .end(new JsonObject()
            .put("error", res.cause().getMessage())
            .toString());
      }
    });
  } catch (Exception e) {
    System.out.println("error " + e);
    ctx.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(500)
      .end(new JsonObject()
        .put("error", "Erreur inattendue")
        .toString());
  }
}

/**
 * @param ctx RoutingContext
 * @author abdellah
 * <p>
 * OpenAPI3 Route getContract
 * request body <JsonObject>
 * </p>
 */
public void getContract(RoutingContext ctx) {
  try {
    JsonObject body = ctx.getBodyAsJson();

    vertx.eventBus().request(Services.CONTRACT_GET, body, res -> {
      if (res.succeeded()) {
        JsonObject response = (JsonObject) res.result().body();
        if (response.isEmpty()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(404)
            .end(new JsonObject()
              .put("error", "Contract not found")
              .toString());
        } else {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(response.toString());
        }
      } else {
        ctx.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(res.cause().getMessage().contains("ID du contrat requis") ? 400 : 500)
          .end(new JsonObject()
            .put("error", res.cause().getMessage())
            .toString());
      }
    });
  } catch (Exception e) {
    System.out.println("error " + e);
    ctx.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(500)
      .end(new JsonObject()
        .put("error", "Erreur inattendue")
        .toString());
  }
}

/**
 * @param ctx RoutingContext
 * @author abdellah
 * <p>
 * OpenAPI3 Route uploadFile
 * request body <multipart/form-data>
 * </p>
 */
public void uploadFile(RoutingContext ctx) {
  try {
    List<FileUpload> uploads = ctx.fileUploads();
    if (uploads.isEmpty()) {
      ctx.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(400)
        .end(new JsonObject()
          .put("error", "Aucun fichier n'a été fourni")
          .toString());
      return;
    }

    FileUpload upload = uploads.iterator().next();
    String fileName = upload.fileName();
    String uploadedFilePath = upload.uploadedFileName();


    JsonObject fileInfo = new JsonObject()
      .put("fileName", fileName)
      .put("uploadedPath", uploadedFilePath);


    vertx.eventBus().request(Services.FILE_DOWNLOAD, fileInfo, res -> {
      if (res.succeeded()) {
        JsonObject response = new JsonObject()
          .put("message", "Fichier téléchargé avec succès")
          .put("file", res.result().body());

        ctx.response()
          .putHeader("content-type", "application/json")
          .end(response.toString());
      } else {
        ctx.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(500)
          .end(new JsonObject()
            .put("error", res.cause().getMessage())
            .toString());
      }
    });

  } catch (Exception e) {
    System.out.println("error " + e);
    ctx.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(500)
      .end(new JsonObject()
        .put("error", "Erreur lors du téléchargement du fichier")
        .toString());
  }
}

/**
 * @param ctx RoutingContext
 * @author abdellah
 * <p>
 * OpenAPI3 Route getFiles
 * request body <JsonObject>
 * </p>
 */
public void getFiles(RoutingContext ctx) {
  try {
      JsonObject body = ctx.getBodyAsJson();
      if (body == null) {
          body = new JsonObject();
      }


      JsonObject options = new JsonObject();
      String pageStr = ctx.request().getParam("page");
      String limitStr = ctx.request().getParam("limit");


      int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
      int limit = limitStr != null ? Integer.parseInt(limitStr) : 10;

      options.put("page", page)
             .put("limit", limit);

      body.put("options", options);


      vertx.eventBus().request(Services.FILE_GET, body, res -> {
          if (res.succeeded()) {
              ctx.response()
                  .putHeader("content-type", "application/json")
                  .end(res.result().body().toString());
          } else {
              ctx.response()
                  .putHeader("content-type", "application/json")
                  .setStatusCode(500)
                  .end(new JsonObject()
                      .put("error", res.cause().getMessage())
                      .toString());
          }
      });
  } catch (Exception e) {
      ctx.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(500)
          .end(new JsonObject()
              .put("error", "Erreur lors de la récupération des fichiers: " + e.getMessage())
              .toString());
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

    vertx.eventBus().request(Services.NOTIFICATION_LIST, body, res -> {
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
    vertx.deployVerticle(new Contract());
    vertx.deployVerticle(new File());
    vertx.deployVerticle(new AuthVerticle());
    vertx.deployVerticle(new PdfGenerator());
    vertx.deployVerticle(new Notifications());
  }
}


