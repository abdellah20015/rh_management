package com.example.rh.services;

import java.util.stream.Collectors;

import com.example.rh.constants.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.AggregateOptions;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

public class Db extends AbstractVerticle {

    private MongoClient mongoClient;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        mongoClient = Conf.createMongoClient(vertx);


        vertx.eventBus().consumer(Services.DB_INSERT, this::insertDocument);
        vertx.eventBus().consumer(Services.DB_FIND, this::findDocuments);
        vertx.eventBus().consumer(Services.DB_UPDATE, this::updateDocument);
        vertx.eventBus().consumer(Services.DB_REMOVE_DOCUMENT, this::deleteDocument);
        vertx.eventBus().consumer(Services.DB_FIND_ONE, this::findOneDocument);
        vertx.eventBus().consumer(Services.DB_FIND_WITH_OPTIONS, this::findWithOptions);
        vertx.eventBus().consumer(Services.DB_COUNT, this::count);
        vertx.eventBus().consumer(Services.DB_AGGREGATE, this::aggregate);
        vertx.eventBus().consumer(Services.DB_REMOVE_DOCUMENTS, this::removeDocuments);
        vertx.eventBus().consumer(Services.DB_UPDATE_COLLECTION_WITH_OPTIONS, this::updateCollectionWithOptions);

        startPromise.complete();
    }

    private void insertDocument(Message<JsonObject> message) {
      JsonObject payload = message.body();
      String collection = payload.getString("collection");
      JsonObject document = payload.getJsonObject("query");

      mongoClient.insert(collection, document, res -> {
          if (res.succeeded()) {
              JsonObject query = new JsonObject().put("_id", res.result());
              mongoClient.findOne(collection, query, null, findRes -> {
                  JsonObject response = new JsonObject();

                  if (findRes.succeeded() && findRes.result() != null) {
                      response.put("status", "success")
                             .put("message", "Document inséré avec succès")
                             .put("data", findRes.result());
                  } else {
                      response.put("status", "error")
                             .put("message", "Document inséré mais impossible de le récupérer")
                             .put("code", -1);
                  }

                  message.reply(response);
              });
          } else {
              JsonObject response = new JsonObject()
                  .put("status", "error")
                  .put("message", "Échec de l'insertion: " + res.cause().getMessage())
                  .put("code", -1);
              message.reply(response);
          }
      });
  }

  private void findDocuments(Message<JsonObject> message) {
      JsonObject payload = message.body();
      String collection = payload.getString("collection");
      JsonObject query = payload.getJsonObject("query", new JsonObject());

      mongoClient.find(collection, query, res -> {
          JsonObject response = new JsonObject();
          if (res.succeeded()) {
//              response.put("status", "success")
//                     .put("data", new JsonArray(res.result()));
              mongoClient.count(collection, query, countRes -> {
                if(countRes.succeeded()) {
                  message.reply(response.put("status", "success")
                     .put("count", countRes.result())
                    .put("data", new JsonArray(res.result())));
                }
              });
          } else {
            message.reply(response.put("status", "error")
                     .put("message", "Échec de la recherche: " + res.cause().getMessage())
                     .put("code", -1));
          }

//          message.reply(response);
      });
  }

  private void updateDocument(Message<JsonObject> message) {
    JsonObject payload = message.body();
    String collection = payload.getString("collection");
    String id = payload.getString("id");
    JsonObject update = payload.getJsonObject("update");

    JsonObject query = new JsonObject().put("_id", id);
    JsonObject updateDoc = new JsonObject().put("$set", update);

    mongoClient.updateCollection(collection, query, updateDoc, res -> {
        if (res.succeeded() && res.result().getDocModified() > 0) {

            mongoClient.findOne(collection, query, null, findRes -> {
                JsonObject response = new JsonObject();

                if (findRes.succeeded() && findRes.result() != null) {
                    response.put("status", "success")
                           .put("message", "Document mis à jour avec succès")
                           .put("data", findRes.result());
                } else {
                    response.put("status", "error")
                           .put("message", "Document mis à jour mais impossible de le récupérer")
                           .put("code", -1);
                }

                message.reply(response);
            });
        } else {
            JsonObject response = new JsonObject()
                .put("status", "error")
                .put("message", "Document non trouvé")
                .put("code", 404);
            message.reply(response);
        }
    });
}

private void deleteDocument(Message<JsonObject> message) {
  JsonObject payload = message.body();
  String collection = payload.getString("collection");
  String id = payload.getString("id");

  JsonObject query = new JsonObject().put("_id", id);


  mongoClient.findOne(collection, query, null, findRes -> {
      if (findRes.succeeded() && findRes.result() != null) {
          JsonObject documentToDelete = findRes.result();


          mongoClient.removeDocument(collection, query, deleteRes -> {
              JsonObject response = new JsonObject();

              if (deleteRes.succeeded() && deleteRes.result().getRemovedCount() > 0) {
                  response.put("status", "success")
                         .put("message", "Document supprimé avec succès")
                         .put("data", documentToDelete);
              } else {
                  response.put("status", "error")
                         .put("message", "Échec de la suppression")
                         .put("code", -1);
              }

              message.reply(response);
          });
      } else {
          JsonObject response = new JsonObject()
              .put("status", "error")
              .put("message", "Document non trouvé")
              .put("code", 404);
          message.reply(response);
      }
  });
}

  private void findOneDocument(Message<JsonObject> message) {
    JsonObject payload = message.body();
    String collection = payload.getString("collection");
    JsonObject query = payload.getJsonObject("query");

        mongoClient.findOne(collection, query, null, res -> {
            if (res.succeeded() && res.result() != null) {
                message.reply(res.result());
            } else {
                message.reply(res.result());
            }
        });
    }

private void findWithOptions(Message<JsonObject> message) {
    JsonObject payload = message.body();
    String collection = payload.getString("collection");
    JsonObject query = payload.getJsonObject("query");
    FindOptions options = new FindOptions(payload.getJsonObject("options"));

    mongoClient.findWithOptions(collection, query, options, res -> {
        JsonObject response = new JsonObject();

        if (res.succeeded()) {
            response.put("status", "success")
                   .put("data", new JsonArray(res.result()));
        } else {
            response.put("status", "error")
                   .put("message", res.cause().getMessage())
                   .put("code", 500);
        }

        message.reply(response);
    });
}

private void count(Message<JsonObject> message) {
    JsonObject payload = message.body();
    String collection = payload.getString("collection");
    JsonObject query = payload.getJsonObject("query");

    mongoClient.count(collection, query, res -> {
        JsonObject response = new JsonObject();

        if (res.succeeded()) {
            response.put("status", "success")
                   .put("count", res.result());
        } else {
            response.put("status", "error")
                   .put("message", res.cause().getMessage())
                   .put("code", 500);
        }

        message.reply(response);
    });
}

  private void aggregate(Message<JsonObject> message) {
    JsonObject payload = message.body();
    String collection = payload.getString("collection");
    JsonArray pipeline = payload.getJsonArray("pipeline");
    AggregateOptions options = new AggregateOptions(payload.getJsonObject("options"));

    // Create a simple $count stage pipeline
    JsonArray countPipeline = new JsonArray();
    for (Object stage : pipeline) {
      JsonObject stageObj = (JsonObject) stage;
      // Skip $limit stage for count
      if (!stageObj.containsKey("$limit")) {
        countPipeline.add(stage);
      }
    }
    countPipeline.add(new JsonObject().put("$count", "total"));

    //get the count
    mongoClient
      .aggregateWithOptions(collection, countPipeline, options)
      .collect(Collectors.toList())
      .onComplete(countRes -> {
        if (countRes.succeeded()) {
          //get the data
          mongoClient
            .aggregateWithOptions(collection, pipeline, options)
            .collect(Collectors.toList())
            .onComplete(dataRes -> {
              JsonObject response = new JsonObject();

              if (dataRes.succeeded()) {
                long count = countRes.result().isEmpty() ? 0 : countRes.result().get(0).getLong("total");

                response.put("status", "success")
                  .put("count", count)
                  .put("data", new JsonArray(dataRes.result()));

              } else {
                response.put("status", "error")
                  .put("message", dataRes.cause().getMessage())
                  .put("code", 500);
              }

              message.reply(response);
            });
        } else {
          JsonObject response = new JsonObject()
            .put("status", "error")
            .put("message", countRes.cause().getMessage())
            .put("code", 500);

          message.reply(response);
        }
      });
  }

private void removeDocuments(Message<JsonObject> message) {
  JsonObject payload = message.body();
  String collection = payload.getString("collection");
  JsonObject query = payload.getJsonObject("query");

  mongoClient.find(collection, query, findRes -> {
      if (findRes.succeeded() && !findRes.result().isEmpty()) {
          JsonArray documentsToDelete = new JsonArray(findRes.result());

          mongoClient.removeDocuments(collection, query, deleteRes -> {
              JsonObject response = new JsonObject();

              if (deleteRes.succeeded()) {
                  response.put("status", "success")
                         .put("message", "Documents supprimés avec succès")
                         .put("data", documentsToDelete);
              } else {
                  response.put("status", "error")
                         .put("message", "Échec de la suppression: " + deleteRes.cause().getMessage())
                         .put("code", -1);
              }

              message.reply(response);
          });
      } else {
          JsonObject response = new JsonObject()
              .put("status", "error")
              .put("message", "Aucun document trouvé")
              .put("code", 404);
          message.reply(response);
      }
  });
}

private void updateCollectionWithOptions(Message<JsonObject> message) {
    JsonObject payload = message.body();
    String collection = payload.getString("collection");
    JsonObject query = payload.getJsonObject("query");
    JsonObject update = payload.getJsonObject("update");
    UpdateOptions options = new UpdateOptions(payload.getJsonObject("options"));

    mongoClient.updateCollectionWithOptions(collection, query, update, options, res -> {
        JsonObject response = new JsonObject();

        if (res.succeeded()) {
            response.put("status", "success")
                   .put("message", "Mise à jour effectuée avec succès");
        } else {
            response.put("status", "error")
                   .put("message", res.cause().getMessage())
                   .put("code", 500);
        }

        message.reply(response);
    });
}
}
