package com.example.rh.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.file.FileSystem;


public class File extends AbstractVerticle {

  private static final String UPLOAD_DIR = "uploads";

  @Override
  public void start() {
    try {
      FileSystem fs = vertx.fileSystem();
      fs.mkdirs(UPLOAD_DIR, res -> {
        if (!res.succeeded()) {
          System.err.println("Failed to create upload directory: " + res.cause());
        }
      });

      vertx.eventBus().consumer(Services.FILE_DOWNLOAD, this::uploadFileHandler);
      vertx.eventBus().consumer(Services.FILE_GET, this::getFileHandler);
      vertx.eventBus().consumer(Services.FILE_DOWNLOAD_PDF, this::downloadFileHandler);
    } catch(Exception e) {
      System.out.println("Error starting File verticle: " + e);
    }
  }

  /**
   * @param message Message
   * @author abdellah
   * <p>
   * Excel file upload handler
   * Stores the file in the uploads folder and saves the metadata in the DB
   * </p>
   */
  private void uploadFileHandler(Message<JsonObject> message) {
    try {
      JsonObject body = message.body();

      if (!validateFileFields(body)) {
        message.fail(400, "Informations de fichier manquantes");
        return;
      }

      String originalFilename = body.getString("fileName");
      String uploadedFilePath = body.getString("uploadedPath");


      if (!originalFilename.toLowerCase().endsWith(".xlsx") && !originalFilename.toLowerCase().endsWith(".xls")) {
        message.fail(400, "Format de fichier non supporté. Seuls les fichiers Excel (.xlsx, .xls) sont acceptés");
        return;
      }

      String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
      Path uploadedPath = Paths.get(uploadedFilePath);
      String tempFileName = uploadedPath.getFileName().toString();
      String newFilePath = Paths.get(UPLOAD_DIR, tempFileName + fileExtension).toString();

      vertx.fileSystem().move(uploadedFilePath, newFilePath, moveResult -> {
        if (moveResult.succeeded()) {
          JsonObject importMessage = new JsonObject()
            .put("path", newFilePath);

          vertx.eventBus().request(Services.USER_IMPORT, importMessage, importRes -> {
            if (importRes.succeeded()) {
              JsonObject result = (JsonObject) importRes.result().body();


              JsonObject fileDocument = new JsonObject()
                .put(Fields.FILE_NAME, originalFilename)
                .put(Fields.FILE_PATH, newFilePath)
                .put(Fields.FILE_TOTAL_LINES, result.getInteger("totalLines"))
                .put(Fields.FILE_SUCCESSFUL_LINES, result.getInteger("successLines"))
                .put(Fields.FILE_ERROR_LINES, result.getInteger("errorLines"))
                .put(Fields.FILE_DATE_CREATION, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis())));


              JsonObject dbMessage = new JsonObject()
                .put("collection", Collections.FILES)
                .put("query", fileDocument);

              vertx.eventBus().request(Services.DB_INSERT, dbMessage, res -> {
                if (res.succeeded()) {
                  message.reply(res.result().body());
                } else {
                  vertx.fileSystem().delete(newFilePath, deleteRes -> {
                    if (deleteRes.failed()) {
                      System.err.println("Failed to delete file: " + deleteRes.cause());
                    }
                  });
                  message.fail(500, res.cause().getMessage());
                }
              });
            } else {
              vertx.fileSystem().delete(newFilePath, deleteRes -> {
                if (deleteRes.failed()) {
                  System.err.println("Failed to delete file: " + deleteRes.cause());
                }
              });
              message.fail(500, "Erreur lors du traitement du fichier: " + importRes.cause().getMessage());
            }
          });
        } else {
          message.fail(500, "Erreur lors du renommage du fichier");
        }
      });

    } catch(Exception e) {
      System.err.println("Error in uploadFileHandler: " + e);
      message.fail(500, "Erreur inattendue lors du traitement du fichier");
    }
  }

  /**
   * @param message Message
   * @author abdellah
   * <p>
   * Handler to retrieve information from a file
   * </p>
   */
  private void getFileHandler(Message<JsonObject> message) {
    try {
        JsonObject body = message.body();

        JsonObject options = body.getJsonObject("options", new JsonObject());
        int page = options.getInteger("page", 1);
        int limit = options.getInteger("limit", 10);

        int skip = (page - 1) * limit;

        JsonObject query = body.getJsonObject("query", new JsonObject());

        JsonObject countMsg = new JsonObject()
            .put("collection", Collections.FILES)
            .put("query", query);

        JsonObject findMsg = new JsonObject()
            .put("collection", Collections.FILES)
            .put("query", query)
            .put("skip", skip)
            .put("limit", limit)
            .put("sort", new JsonObject().put(Fields.FILE_DATE_CREATION, -1));


        vertx.eventBus().request(Services.DB_COUNT, countMsg, countRes -> {
            if (countRes.succeeded()) {
                JsonObject countResult = (JsonObject) countRes.result().body();
                long total = countResult.getLong("count", 0L);

                vertx.eventBus().request(Services.DB_FIND, findMsg, findRes -> {
                    if (findRes.succeeded()) {
                        JsonObject response = new JsonObject()
                            .put("total", total)
                            .put("page", page)
                            .put("limit", limit)
                            .put("totalPages", Math.ceil((double) total / limit))
                            .put("files", findRes.result().body());

                        message.reply(response);
                    } else {
                        message.fail(500, findRes.cause().getMessage());
                    }
                });
            } else {
                message.fail(500, countRes.cause().getMessage());
            }
        });

    } catch(Exception e) {
        message.fail(500, "Erreur lors de la récupération des fichiers: " + e.getMessage());
    }
  }

  /**
   * @param message Message
   * @author abdellah
   * <p>
   * Handler to download file
   * </p>
   */

   private void downloadFileHandler(Message<JsonObject> message) {
    try {
        JsonObject body = message.body();
        String filepath = body.getString("filepath");

        if (filepath == null || filepath.isEmpty()) {
            message.fail(400, "Chemin du fichier manquant");
            return;
        }

        String fullPath = Paths.get(filepath).toString();

        vertx.fileSystem().exists(fullPath, existResult -> {
            if (existResult.succeeded() && existResult.result()) {
                vertx.fileSystem().readFile(fullPath, readResult -> {
                    if (readResult.succeeded()) {
                        message.reply(new JsonObject()
                            .put("content", readResult.result().getBytes())
                            .put("filepath", filepath)
                        );
                    } else {
                        message.fail(500, "Erreur lors de la lecture du fichier");
                    }
                });
            } else {
                message.fail(404, "Fichier non trouvé");
            }
        });
    } catch(Exception e) {
        System.err.println("Error in downloadFileHandler: " + e);
        message.fail(500, "Erreur inattendue lors du téléchargement du fichier");
    }
}

  /**
   * @param fileInfo JsonObject
   * @author abdellah
   * <p>
   * Checks whether the information in the file is complete
   * </p>
   */
  private boolean validateFileFields(JsonObject fileInfo) {
    return fileInfo != null &&
           fileInfo.containsKey("fileName") &&
           fileInfo.containsKey("uploadedPath") &&
           !fileInfo.getString("fileName").isEmpty() &&
           !fileInfo.getString("uploadedPath").isEmpty();
  }
}
