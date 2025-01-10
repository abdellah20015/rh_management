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
        JsonObject document = payload.getJsonObject("document");

        mongoClient.insert(collection, document, res -> {
            if (res.succeeded()) {
                message.reply(new JsonObject().put("id", res.result()));
            } else {
                message.fail(-1, "Insert failed: " + res.cause().getMessage());
            }
        });
    }

    private void findDocuments(Message<JsonObject> message) {
        JsonObject payload = message.body();
        String collection = payload.getString("collection");
        JsonObject query = payload.getJsonObject("query", new JsonObject());

        mongoClient.find(collection, query, res -> {
            if (res.succeeded()) {
                message.reply(new JsonArray(res.result()));
            } else {
                message.fail(-1, "Find failed: " + res.cause().getMessage());
            }
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
                message.reply(new JsonObject().put("status", "success"));
            } else {
                message.fail(404, "Update failed: Document not found");
            }
        });
    }

    private void deleteDocument(Message<JsonObject> message) {
        JsonObject payload = message.body();
        String collection = payload.getString("collection");
        String id = payload.getString("id");

        JsonObject query = new JsonObject().put("_id", id);

        mongoClient.removeDocument(collection, query, res -> {
            if (res.succeeded() && res.result().getRemovedCount() > 0) {
                message.reply(new JsonObject().put("status", "success"));
            } else {
                message.fail(404, "Delete failed: Document not found");
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
                // message.fail(404, "Document not found");
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
            if (res.succeeded()) {
                message.reply(new JsonArray(res.result()));
            } else {
                message.fail(500, res.cause().getMessage());
            }
        });
    }

    private void count(Message<JsonObject> message) {
        JsonObject payload = message.body();
        String collection = payload.getString("collection");
        JsonObject query = payload.getJsonObject("query");

        mongoClient.count(collection, query, res -> {
            if (res.succeeded()) {
                message.reply(new JsonObject().put("count", res.result()));
            } else {
                message.fail(500, res.cause().getMessage());
            }
        });
    }

    private void aggregate(Message<JsonObject> message) {
        JsonObject payload = message.body();
        String collection = payload.getString("collection");
        JsonArray pipeline = payload.getJsonArray("pipeline");
        AggregateOptions options = new AggregateOptions(payload.getJsonObject("options"));

        mongoClient
            .aggregateWithOptions(collection, pipeline, options)
            .collect(Collectors.toList())
            .onComplete(res -> {
                if (res.succeeded()) {
                    message.reply(new JsonArray(res.result()));
                } else {
                    message.fail(500, res.cause().getMessage());
                }
            });
    }

    private void removeDocuments(Message<JsonObject> message) {
        JsonObject payload = message.body();
        String collection = payload.getString("collection");
        JsonObject query = payload.getJsonObject("query");

        mongoClient.removeDocuments(collection, query, res -> {
            if (res.succeeded()) {
                message.reply(new JsonObject().put("status", "success"));
            } else {
                message.fail(500, res.cause().getMessage());
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
            if (res.succeeded()) {
                message.reply(new JsonObject().put("status", "success"));
            } else {
                message.fail(500, res.cause().getMessage());
            }
        });
    }
}
