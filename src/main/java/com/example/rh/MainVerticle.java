package com.example.rh;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Services;
import com.example.rh.services.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
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
  Map<String, ServerWebSocket> userWebSockets = new HashMap<>();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String path = "src/main/api/openapi.json";
    Conf.createMongoClient(vertx);
    OpenAPIContract.from(vertx, path)
    .onSuccess(contract -> {
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


      routerBuilder.rootHandler(ctx ->{
        if (ctx.normalizedPath().startsWith("/private")) {
          handleAuth(ctx);
        }else{
          ctx.next();
          return;
        }
      });
      //demands
      // path: /private/demand/list
      routerBuilder.getRoute("listDemandsByManager").addHandler(this::getListDemandsByManager);
      routerBuilder.getRoute("listDemandsByUser").addHandler(this::getListDemandsByUser);

      // path: /private/demand/create
      routerBuilder.getRoute("createDemand").addHandler(this::createDemand);
      // path: /private/demand/update
      routerBuilder.getRoute("updateDemand").addHandler(ctx -> { handlePermission(ctx, "update_demand"); }).addHandler(this::updateDemand);
      // path : /private/demand/stats
      routerBuilder.getRoute("getDemandStats").addHandler(this::getDemandStats);
      //notificaiton
      routerBuilder.getRoute("getNotifications").addHandler(this::getNotification);
      routerBuilder.getRoute("updateNotificationStatus").addHandler(this::updateNotificationStatus);

       // path  /check
      routerBuilder.getRoute("checkAuth").addHandler(this::handleCheck);

      // path: /login
      routerBuilder.getRoute("login").addHandler(this::LoginHandler);

      // path: /private/logout
      routerBuilder.getRoute("logout").addHandler(this::LogoutHandler);

      // path: /private/resetPassword
      routerBuilder.getRoute("resetPassword").addHandler(this::resetPasswordHandler);

      //  path : /private/user/list
      routerBuilder.getRoute("listUsers").addHandler(ctx -> { handlePermission(ctx, "view_users"); }).addHandler(this::ListUsersHandler);
      //  path : /private/user/manager
      routerBuilder.getRoute("getManager").addHandler(this::getManager);

      // path : /private/user/create
      routerBuilder.getRoute("createUser").addHandler(ctx -> { handlePermission(ctx, "create_user"); }).addHandler(this::createUserHandler);

      // path : /private/user/update
      routerBuilder.getRoute("updateUser").addHandler(ctx -> { handlePermission(ctx, "update_user"); }).addHandler(this::updateUserHandler);

      // path : /private/user/delete
      routerBuilder.getRoute("deleteUser").addHandler(ctx -> { handlePermission(ctx, "delete_user"); }).addHandler(this::DeleteUserHandler);

      // path : /private/user/profile
      routerBuilder.getRoute("getUserProfile").addHandler(this::getUserProfileHandler);
      // path : /private//user/stats
      routerBuilder.getRoute("getStats").addHandler(this::usersCount);

      // Contracts
      routerBuilder.getRoute("createContract").addHandler(ctx -> { handlePermission(ctx, "create_contract"); }).addHandler(this::createContract);
      routerBuilder.getRoute("updateContract").addHandler(ctx -> { handlePermission(ctx, "update_contract"); }).addHandler(this::updateContract);
      routerBuilder.getRoute("getContract").addHandler(this::getContract);
      routerBuilder.getRoute("getExpiringContracts").addHandler(this::getExpiringContracts);

      // Files
      routerBuilder.getRoute("uploadFile").addHandler(ctx -> { handlePermission(ctx, "import_user"); }).addHandler(this::uploadFile);
      routerBuilder.getRoute("getfiles").addHandler(ctx -> { handlePermission(ctx, "import_user"); }).addHandler(this::getFiles);
      routerBuilder.getRoute("downloadFile").addHandler(this::downloadFile);

      // Create a router
      Router router = routerBuilder.createRouter();

      //  create a static handler for the uploads directory
      router.route("/uploads/*").handler(StaticHandler.create("uploads"));

      //webSocket connection
      router.route("/notification").handler(ctx -> {
        if (ctx.user() != null) {
          ctx.request().toWebSocket().onSuccess(ws -> {
              JsonObject user = ctx.user().principal();
              String userId = user.getString("id");
              userWebSockets.put(userId, ws);
              System.out.println("\n user object ws : " + user + "\n");
              System.out.println("websocket connected");


              vertx.eventBus().consumer(Services.NOTIFICATION_SEND, msg -> {
                JsonObject notificationObject = (JsonObject) msg.body();
                JsonObject notificationData = notificationObject.getJsonObject("data");

                System.out.println("\n user id ws : " + user.getString("id") + "\n");
                System.out.println("\n user noti id ws : " + notificationData.getString("user_id") + "\n");


                ServerWebSocket targetWs = userWebSockets.get(notificationData.getString("user_id"));
                if (targetWs != null) {
                  targetWs.writeTextMessage(notificationData.toString())
                    .onSuccess(res -> System.out.println("Notification has been sent!"));
                } else {
                  System.out.println("No WebSocket connection found for user ID: " + notificationData.getString("user_id"));
                }
              });

              // Close handler
              ws.closeHandler(handle -> {
                System.out.println("connection closed");
                userWebSockets.remove(userId);
              });

              // Incoming message handler
              ws.handler(buffer -> {
                String message = buffer.toString();
                System.out.println("Received WebSocket message: " + message);
              });
          }).onFailure(err -> {
            System.err.println("Failed to upgrade to WebSocket: " + err.getMessage());
            ctx.fail(err);
          });
        }else {
          System.out.println("User not authenticated. Closing WebSocket.");
          ctx.response().setStatusCode(401).end("Unauthorized");
        }
      });

      //  create http server and listen on port 8888
      vertx.createHttpServer().requestHandler(router).listen(PORT)
      .onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port " + PORT);
        } else {
          startPromise.fail(http.cause());
        }
      });
    })
    .onFailure(err -> {
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
  private void getListDemandsByManager(RoutingContext ctx) {
    try {
      JsonObject body = ctx.getBodyAsJson();
      JsonObject user = ctx.user().principal();
      body.put("user" , user);

      vertx.eventBus().request(Services.DEMAND_LIST_MANAGER, body, res -> {
        if (res.succeeded()) {
          ctx.response()
            .setStatusCode(200)
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
   * OpenAPI3 Route getListDemands
   * request body <JsonObject>
   * </p>
   */
  private void getListDemandsByUser(RoutingContext ctx) {
    try {
      JsonObject body = ctx.getBodyAsJson();
      JsonObject user = ctx.user().principal();
      body.put("user" , user);

      vertx.eventBus().request(Services.DEMAND_LIST_USER, body, res -> {
        if (res.succeeded()) {
          ctx.response()
            .setStatusCode(200)
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
            .setStatusCode(201)
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
 * @author : Abdellah
 * <p>
 * This function handles an HTTP request to retrieve the list of contracts
 * expiring within the next 10 days. It sends the result as a JSON response
 * to the client.
 * </p>
 */

private void getExpiringContracts(RoutingContext ctx) {
  JsonObject msg = new JsonObject()
    .put("user_id" , ctx.user().principal().getString("id"));


  vertx.eventBus().request(Services.DB_EXPIRING_CONTRACTS, msg, res -> {
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
 * @author abdellah
 * <p>
 * OpenAPI3 Route DownloadFile
 * request body <JsonObject>
 * </p>
 */
public void downloadFile(RoutingContext ctx) {
  try {
      String filepath = ctx.body().asJsonObject().getString("filepath");
      JsonObject fileInfo = new JsonObject()
          .put("filepath", filepath);

      vertx.eventBus().request(Services.FILE_DOWNLOAD_PDF, fileInfo, res -> {
          if (res.succeeded()) {
              JsonObject response = (JsonObject) res.result().body();
              byte[] content = response.getBinary("content");
              String filename = Paths.get(response.getString("filepath")).getFileName().toString();



              if (filename.toLowerCase().endsWith(".pdf")) {
                  ctx.response()
                      .putHeader("Content-Type", "application/pdf")
                      .putHeader("Content-Disposition", "inline; filename=\"" + filename + "\"")
                      .sendFile("uploads/" + filename);
              } else {
                  ctx.response()
                      .putHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                      .sendFile("uploads/" + filename);
              }

          } else {
              String error = res.cause().getMessage();
              int statusCode = error.contains("non trouvé") ? 404 : 500;

              ctx.response()
                  .setStatusCode(statusCode)
                  .putHeader("content-type", "application/json")
                  .end(new JsonObject()
                      .put("error", error)
                      .toString());
          }
      });
  } catch (Exception e) {
      ctx.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json")
          .end(new JsonObject()
              .put("error", "Erreur lors du téléchargement du fichier: " + e.getMessage())
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
   * @param ctx RoutingContext
   * @author Youssef
   * <p>
   * OpenAPI3 Route updateNotifications
   * request body <JsonObject>
   * </p>
   */
  public void updateNotificationStatus(RoutingContext ctx) {
    JsonObject body = ctx.getBodyAsJson();

//    try {
      vertx.eventBus().request(Services.NOTIFICATION_UPDATE_STATUS, body, res -> {
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
//    }catch(Exception e) {
//      System.out.println("error " + e);
//    }
  }

    /**
   * @author ilyass
   * @param ctx
   *handleAuth Checks user authentication for the current request
   */

   public static void  handleAuth(RoutingContext ctx){
    try {
      if (ctx.user() == null || ctx.user().principal().getBoolean("status") == null) {
          ctx.response()
              .setStatusCode(401)
              .putHeader("content-type", "application/json")
              .end(new JsonObject().put("error", "Unauthorized").encode());
      }
      else{
          ctx.next();
      }
    } catch (Exception e) {
      ctx.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("error", "Internal Server Error").encode());
    }
  }


    /**
 * @author ilyass
 * @param ctx
 * @param permission string
 * handlePermission checks user permission for the current request
 */
public void handlePermission(RoutingContext ctx, String permission) {
  try {
    if (ctx.user() != null) {
      JsonArray permissions = ctx.user().principal().getJsonArray("permissions");
      if (permissions != null && permissions.contains(permission)) {
        ctx.next();
      } else {
        ctx.response()
          .setStatusCode(403)
          .putHeader("content-type", "application/json")
          .end(new JsonObject()
            .put("error", "Forbidden: Insufficient permission")
            .put("required", permission)
            .put("user_permissions", permissions)
            .encode());
      }
    } else {
      ctx.response()
        .setStatusCode(401)
        .putHeader("content-type", "application/json")
        .end(new JsonObject().put("error", "Unauthorized").encode());
    }
  } catch (Exception e) {
    ctx.response()
      .setStatusCode(500)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", "Internal Server Error").encode());
  }
}

  /**
   * Login handler
   *
   * @author Ilyass
   *         login method to authenticate the user and create a session for him
   */
  public void LoginHandler(RoutingContext ctx) {
    try {
      JsonObject body = ctx.body().asJsonObject();
      vertx.eventBus().request(Services.AUTH_LOGIN, body , reply ->{
        if(reply.succeeded() && reply.cause() == null){
          JsonObject response = (JsonObject) reply.result().body();
          if (response.containsKey("error")) {
            ctx.response()
              .setStatusCode(401)
              .putHeader("content-type", "application/json")
              .end(response.encode());
            return;

          }

          User user = User.create(response.getJsonObject("user"));
          ctx.setUser(user);
          ctx.session().regenerateId();
          ctx.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(response.encode());
        }else{
          ctx.response().setStatusCode(401).end(reply.cause().getMessage());
        }
      });
    } catch (Exception e) {
      ctx.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("error", "Internal Server Error").encode());
    }


  }
  /**
   * Logout handler
   *
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

  public void handleCheck(RoutingContext ctx) {
    if (ctx.user() != null) {
      JsonObject userData = new JsonObject()
          .put("username", ctx.user().principal().getString("username"))
          .put("id", ctx.user().principal().getString("id"))
          .put("role", ctx.user().principal().getString("role"))
          .put("status", ctx.user().principal().getBoolean("status"))
          .put("first_login", ctx.user().principal().getBoolean("first_login"))
          .put("permissions", ctx.user().principal().getJsonArray("permissions"));

          ctx.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(userData.encode());
    } else {
      ctx.response()
      .setStatusCode(401)
      .putHeader("Content-Type", "application/json")
      .end(new JsonObject().put("message", "Not authenticated").encode());
    }
  }

  /**
   * Reset password handler
   *
   * @author ilyass
   *         reset password method to reset the password of the user
   */
  public void resetPasswordHandler(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    body.put("username", ctx.user().principal().getString("username"));
    vertx.eventBus().request(Services.AUTH_RESET_PASSWORD, body, reply -> {
      if (reply.succeeded()) {
        ctx.user().principal().put("first_login", false);
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
   *
   * @param ctx RoutingContext
   * @author ilyass
   * list users method to list all the users with aggregation
   */
  public void ListUsersHandler(RoutingContext ctx) {
    try {
      JsonObject body = ctx.body().asJsonObject();
      JsonObject option = body.getJsonObject("query");
      JsonObject filter = option.getJsonObject("filter");
      String search = option.getString("search");
      int page = option.getInteger("page");
      int limit = option.getInteger("limit");
      int skip = (page - 1) * limit;

      JsonObject match = new JsonObject();
      if (!ctx.user().principal().getString("role").equals("admin")) {
        match.put("manager_id", ctx.user().principal().getString("id"));
      }

      JsonObject lookupContracts = new JsonObject()
          .put("from", Collections.CONTRACTS)
          .put("localField", "_id")
          .put("foreignField", "user_id")
          .put("as", "contracts");

      JsonArray pipeline = new JsonArray()
        .add(new JsonObject().put("$match", match))
        .add(new JsonObject().put("$lookup", lookupContracts))
        .add(new JsonObject().put("$unwind", new JsonObject()
          .put("path" , "$contracts")
          .put("preserveNullAndEmptyArrays", true)))
        .add(new JsonObject().put("$sort", new JsonObject().put("date_creation", -1)));


      if (search != null && !search.isEmpty()) {
        pipeline.add(new JsonObject().put("$match", new JsonObject()
          .put("$or", new JsonArray()
            .add(new JsonObject()
              .put("username", new JsonObject()
                .put("$regex", ".*" + search.replace(" ", ".*") + ".*")
                .put("$options", "i")))
            .add(new JsonObject()
              .put("firstname", new JsonObject()
                .put("$regex", ".*" + search.replace(" ", ".*") + ".*")
                .put("$options", "i")))
            .add(new JsonObject()
              .put("lastname", new JsonObject()
                .put("$regex", ".*" + search.replace(" ", ".*") + ".*")
                .put("$options", "i"))))));
      }

      if(!filter.isEmpty()){
        if(filter.containsKey("role") && !filter.getJsonArray("role").isEmpty()){
          pipeline.add(new JsonObject().put("$match" , new JsonObject()
            .put("role", new JsonObject()
              .put("$in", filter.getJsonArray("role")))));
        }
        if(filter.containsKey("type") && !filter.getJsonArray("type").isEmpty()){
          pipeline.add(new JsonObject().put("$unwind", "$contracts"))
            .add(new JsonObject().put("$sort", new JsonObject().put("contracts.date_creation", -1)));
          pipeline.add(new JsonObject().put("$match" , new JsonObject()
            .put("contracts.type", new JsonObject()
              .put("$in", filter.getJsonArray("type")))));
        }
      }

      pipeline.add(new JsonObject().put("$skip", skip))
        .add(new JsonObject().put("$limit", limit));


      JsonObject aggregate = new JsonObject()
          .put("collection", Collections.USER)
          .put("pipeline", pipeline)
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
   * users counts
   * @author ilyass
   * methode to get the stats of the users
   */
  public void usersCount(RoutingContext ctx){
    try {
      JsonObject match = new JsonObject();
      if (!ctx.user().principal().getString("role").equals("admin")) {
          match.put("manager_id", ctx.user().principal().getString("id"));
      }

      JsonObject aggregate = new JsonObject()
          .put("collection", Collections.USER)
          .put("pipeline", new JsonArray()
              .add(new JsonObject().put("$match", match))
              .add(new JsonObject().put("$facet", new JsonObject()
                  .put("total", new JsonArray().add(new JsonObject().put("$count", "total")))
                  .put("active", new JsonArray()
                      .add(new JsonObject().put("$match", new JsonObject().put("status", true)))
                      .add(new JsonObject().put("$count", "active"))
                  )
                  .put("inactive", new JsonArray()
                      .add(new JsonObject().put("$match", new JsonObject().put("status", false)))
                      .add(new JsonObject().put("$count", "inactive"))
                  )
              ))
          )
          .put("options", new JsonObject());

      vertx.eventBus().request(Services.DB_AGGREGATE, aggregate ,reply ->{
        if (reply.succeeded()) {
          JsonObject result = (JsonObject) reply.result().body();
          JsonObject data = result.getJsonArray("data").getJsonObject(0);
          JsonObject counts = new JsonObject();
          JsonArray total = data.getJsonArray("total");
          JsonArray active = data.getJsonArray("active");
          JsonArray inactive = data.getJsonArray("inactive");

          counts.put("total_users",(total != null && !total.isEmpty()) ? total.getJsonObject(0).getInteger("total"): 0);
          counts.put("active",(active != null && !active.isEmpty()) ? active.getJsonObject(0).getInteger("active") : 0);
          counts.put("inactive", (inactive != null && !inactive.isEmpty()) ?  inactive.getJsonObject(0).getInteger("inactive"): 0);
          // JsonObject
          ctx.response()
              .setStatusCode(200)
              .putHeader("content-type", "application/json")
              .end(counts.encode());
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
   * getDemandStats
   * @author ilyass
   * @param ctx
   * methode to get the stats of the demand
   */
  public void getDemandStats(RoutingContext ctx){
    try {
      JsonObject match = new JsonObject();
      if (ctx.user().principal().getString("role").equals("manager") || ctx.user().principal().getString("role").equals("admin")) {
          match.put("user.manager_id", ctx.user().principal().getString("id"));
      }
      if (ctx.user().principal().getString("role").equals("employee")) {
          match.put("user_id", ctx.user().principal().getString("id"));

      }

      JsonObject facet = new JsonObject()
      .put("total", new JsonArray().add(new JsonObject().put("$count", "total")))
      .put("pending", new JsonArray()
          .add(new JsonObject().put("$match", new JsonObject().put("status", "pending")))
          .add(new JsonObject().put("$count", "pending"))
      )
      .put("rejected", new JsonArray()
          .add(new JsonObject().put("$match", new JsonObject().put("status", "rejected")))
          .add(new JsonObject().put("$count", "rejected"))
      )
      .put("approved", new JsonArray()
          .add(new JsonObject().put("$match", new JsonObject().put("status", "approved")))
          .add(new JsonObject().put("$count", "approved"))
      )
      .put("last_demands", new JsonArray()
      .add(new JsonObject().put("$sort", new JsonObject().put("created_at", -1)))
      .add(new JsonObject().put("$limit", 5))
  )

;
      if (ctx.user().principal().getString("role").equals("employee")) {
        facet.put("last_pending_demands", new JsonArray()
        .add(new JsonObject().put("$match", new JsonObject().put("status", "pending")))
        .add(new JsonObject().put("$sort", new JsonObject().put("created_at", -1)))
        .add(new JsonObject().put("$limit", 5))
      );

      }
      JsonArray pipeline  = new JsonArray()
       .add(new JsonObject().put("$lookup", new JsonObject()
           .put("from", Collections.USER)
           .put("localField", "user_id")
           .put("foreignField", "_id")
           .put("as", "user")
       )

       )
       .add(new JsonObject().put("$match", match))
       .add(new JsonObject().put("$unwind", new JsonObject().put("path", "$user")))
       .add(new JsonObject().put("$facet", facet));


      JsonObject aggregate = new JsonObject()
      .put("collection", Collections.DEMANDS)
      .put("pipeline", pipeline)
      .put("options", new JsonObject());


  vertx.eventBus().request(Services.DB_AGGREGATE, aggregate ,reply ->{
        if (reply.succeeded()) {
          JsonObject result = (JsonObject) reply.result().body();
          JsonObject data = result.getJsonArray("data").getJsonObject(0);
          JsonArray lastDemands = data.getJsonArray("last_demands");
          JsonArray lastPendingDemands = data.getJsonArray("last_pending_demands");
          JsonObject counts = new JsonObject();
          JsonArray total = data.getJsonArray("total");
          JsonArray approved = data.getJsonArray("approved");
          JsonArray rejected = data.getJsonArray("rejected");
          JsonArray pending = data.getJsonArray("pending");
          counts.put("last_demands", lastDemands);
          if (ctx.user().principal().getString("role").equals("employee")){
            counts.put("last_pending_demands", lastPendingDemands);
          }
          counts.put("total_demands",(total != null && !total.isEmpty()) ? total.getJsonObject(0).getInteger("total"): 0);
          counts.put("approved",(approved != null && !approved.isEmpty()) ? approved.getJsonObject(0).getInteger("approved") : 0);
          counts.put("rejected", (rejected != null && !rejected.isEmpty()) ?  rejected.getJsonObject(0).getInteger("rejected"): 0);
          counts.put("pending", (pending != null && !pending.isEmpty()) ?  pending.getJsonObject(0).getInteger("pending"): 0);
          // JsonObject
          ctx.response()
              .setStatusCode(200)
              .putHeader("content-type", "application/json")
              .end(counts.encode());
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
   * List manager handler
   * @param ctx RoutingContext
   * @author ilyass
   * list manager method to list all the manager
   */

  public void getManager(RoutingContext ctx){
    JsonObject payload = new JsonObject()
                            .put("collection", Collections.USER)
                            .put("query", new JsonObject()
                                .put("role", new JsonObject().put("$in", new JsonArray().add("manager").add("admin"))));
    vertx.eventBus().request(Services.DB_FIND, payload , reply ->{
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

  /**
   * Create user handler
   *
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
   *
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
   *
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
   *
   * @author ilyass
   * get user profile method to get the profile of the user
   */
  public void getUserProfileHandler(RoutingContext ctx) {
    String userId = ctx.body().asJsonObject().getString("user_id");

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
    vertx.deployVerticle(new Contract());
    vertx.deployVerticle(new File());
    vertx.deployVerticle(new PdfGenerator());
    vertx.deployVerticle(new Notifications());

  }
}


