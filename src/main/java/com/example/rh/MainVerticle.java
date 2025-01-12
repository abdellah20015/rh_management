package com.example.rh;





import java.util.List;

import com.example.rh.constants.Services;
import com.example.rh.services.Conf;
import com.example.rh.services.Contract;
import com.example.rh.services.Db;
import com.example.rh.services.Demand;
import com.example.rh.services.File;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
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






  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
    vertx.deployVerticle(new Db());
    vertx.deployVerticle(new Demand());
    vertx.deployVerticle(new Contract());
    vertx.deployVerticle(new File());
  }
}
