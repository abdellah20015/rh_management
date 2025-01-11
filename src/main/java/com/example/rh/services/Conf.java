package com.example.rh.services;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.Vertx;

public class Conf {
    private static final String CONNECTION_STRING = "mongodb+srv://abdellahagnaou92:X823PVMgfZvhezMS@cluster0.lxntw.mongodb.net/";
    private static final String DB_NAME = "rh_management";

    public static MongoClient createMongoClient(Vertx vertx) {
        JsonObject config = new JsonObject()
            .put("connection_string", CONNECTION_STRING)
            .put("db_name", DB_NAME);

        return MongoClient.createShared(vertx, config);
    }
}
