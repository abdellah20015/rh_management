package com.example.rh.services;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.mongo.MongoAuthentication;
import io.vertx.ext.auth.mongo.MongoAuthenticationOptions;
import io.vertx.ext.auth.mongo.MongoUserUtil;
import io.vertx.ext.mongo.MongoClient;

public class AuthVerticle extends AbstractVerticle {
    private MongoClient mongoClient;
    private MongoAuthentication mongoAuth;
    private MongoUserUtil mongoUserUtil;
    private MongoAuthenticationOptions options;

    @Override
    public void start(Promise<Void> startPromise){
        // Create a mongo client
        mongoClient = Conf.createMongoClient(vertx);

        // Create a mongo authentication options object
        options = new MongoAuthenticationOptions()
                        .setCollectionName(Collections.USER)
                        .setUsernameField(Fields.USER_USERNAME)
                        .setPasswordField(Fields.USER_PASSWORD);
        // Create a mongo authentication object with the mongo client and the options
        mongoAuth = MongoAuthentication.create(mongoClient , options);
        // Create a mongo user util object with the mongo client
        mongoUserUtil = MongoUserUtil.create(mongoClient);

        vertx.eventBus().consumer(Services.AUTH_LOGIN, this::login);
        vertx.eventBus().consumer(Services.USER_CREATE, this::createUser);

        startPromise.complete();
    }

    /**
     * Login handler
     * @author ilyass
     * @param message
     * authentificate the user
     */
    public void login (Message<JsonObject> message){
        try {
            JsonObject body = message.body();
            String username = body.getString(Fields.USER_USERNAME);
            String password = body.getString(Fields.USER_PASSWORD);
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            mongoAuth.authenticate(credentials, res -> {
                if (res.succeeded()) {
                    JsonObject user = new JsonObject().put("user", 
                    new JsonObject()
                    .put("username", username)
                    .put("_id", res.result().principal().getString("_id"))
                    .put("role", res.result().principal().getString("role"))
                    .put("permission", res.result().principal().getJsonArray("permission"))
                    );
                    message.reply(user);
                } else {
                    message.fail(401, res.cause().getMessage());
                }
            });
        } catch (Exception e) {
            message.fail(500, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Create user handler
     * @author ilyass
     * @param message
     * create a new user
     */
    public void createUser (Message<JsonObject> message){
        try {
            JsonObject body = message.body();
            String username = body.getString(Fields.USER_USERNAME);
            String password = body.getString(Fields.USER_PASSWORD);
            String role = body.getString(Fields.USER_ROLE);
            JsonObject query = new JsonObject().put(Fields.USER_USERNAME, username);
            JsonObject payload = new JsonObject().put("collection", Collections.USER).put("query", query);

            vertx.eventBus().request(Services.DB_FIND_ONE, payload , reply ->{
                if(reply.succeeded()){
                    if(reply.result().body() == null){
                        mongoUserUtil.createUser(username, password, res -> {
                            if (res.succeeded()) {
                                JsonObject payload2 = new JsonObject().put("collection", Collections.USER).put("id", res.result()).put("update", new JsonObject().put(Fields.USER_ROLE, role));
                                vertx.eventBus().request(Services.DB_UPDATE, payload2 , reply2 ->{
                                    if(reply2.succeeded()){
                                        message.reply(new JsonObject()
                                        .put("message", "User " + username + " created successfully")
                                        );
                                    }else{
                                        message.fail(500, "Internal server error");
                                    }
                                });
                            } else {
                                message.fail(-1, "Insert failed: " + res.cause().getMessage());
                            }
                        });
                    }else{
                        message.fail(400, "User already exists");
                    }
                }else{
                    message.fail(500, "Internal server error" + reply.cause().getMessage());
                }
            });
        } catch (Exception e) {
            message.fail(500, "Internal server error: " + e.getMessage());
        }
    }
}
