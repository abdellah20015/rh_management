package com.example.rh.services;

import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.read.listener.PageReadListener;
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
    private int index = 0;
    private int errLine = 0;
    private int successLine = 0;
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
        vertx.eventBus().consumer(Services.AUTH_RESET_PASSWORD , this::resetPasswordHandler);
        vertx.eventBus().consumer(Services.USER_IMPORT,this::excelCreateHandler);

        startPromise.complete();
    }

    /**
     * Login handler
     * @author ilyass
     * @param message
     * authentificate the user
     */
    private void login (Message<JsonObject> message){
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
                    .put("id", res.result().principal().getString("_id"))
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
    private void createUser (Message<JsonObject> message){
        try {
            JsonObject body = message.body();
            String username = body.getString(Fields.USER_USERNAME);
            String password = body.getString(Fields.USER_PASSWORD);
            String role = body.getString(Fields.USER_ROLE , null);
            String manager_id = body.getString(Fields.USER_MANAGER_ID , null);
            JsonObject query = new JsonObject().put(Fields.USER_USERNAME, username);
            JsonObject payload = new JsonObject().put("collection", Collections.USER).put("query", query);

            vertx.eventBus().request(Services.DB_FIND_ONE, payload , reply ->{
                if(reply.succeeded()){
                    if(reply.result().body() == null){
                        mongoUserUtil.createUser(username, password, res -> {
                            if (res.succeeded()) {
                                JsonObject payload2 = new JsonObject().put("collection", Collections.USER)
                                                                      .put("id", res.result())
                                                                      .put("update", new JsonObject()
                                                                      .put(Fields.USER_ROLE, role)
                                                                      .put(Fields.USER_MANAGER_ID, manager_id)
                                                                      .put(Fields.USER_STATUS, false)
                                                                      .put(Fields.USER_FIRST_LOGIN , true)
                                                                      .put(Fields.USER_DATE_CREATION , System.currentTimeMillis())
                                                                      .put(Fields.USER_PERMISSIONS , null));
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
    /**
     * @author ilyass
     * @param message
     *  methode to resete password
     */
    private void resetPasswordHandler(Message<JsonObject> message) {
        try {
            JsonObject body = message.body();
            String username = body.getString("username");
            String oldPassword = body.getString("old_password");
            String newPassword = body.getString("new_password");

            JsonObject query = new JsonObject().put(Fields.USER_USERNAME, username);
            JsonObject payload = new JsonObject().put("collection", Collections.USER).put("query", query);

            vertx.eventBus().request(Services.DB_FIND_ONE, payload, reply -> {
                if (reply.succeeded() && reply.result().body() != null) {
                    JsonObject user = (JsonObject) reply.result().body();
                    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, oldPassword);
                    mongoAuth.authenticate(credentials, res -> {
                        if (res.succeeded()) {
                            String hashedPassword = mongoAuth.hash("pbkdf2", "salt", newPassword);
                            JsonObject update = new JsonObject().put(Fields.USER_PASSWORD, hashedPassword);
                            JsonObject updatePayload = new JsonObject()
                                .put("collection", Collections.USER)
                                .put("id", user.getString("_id"))
                                .put("update", update);

                            vertx.eventBus().request(Services.DB_UPDATE, updatePayload, updateRes -> {
                                if (updateRes.succeeded()) {
                                    message.reply(new JsonObject().put("message", "Password reset successfully"));
                                } else {
                                    message.fail(500, "Failed to update password: " + updateRes.cause().getMessage());
                                }
                            });
                        } else {
                            message.fail(400, "Old password is incorrect");
                        }
                    });
                } else {
                    message.fail(404, "User not found");
                }
            });
        } catch (Exception e) {
            message.fail(500, "Internal server error: " + e.getMessage());
        }
    }



    public static class SimpleUser {
        @ExcelProperty("username")
        private String username;

        @ExcelProperty("password")
        private String password;


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * Excel create handler
     * @author ilyass
     * @param message
     * Handles the creation of users from an Excel file
     */
    private void excelCreateHandler(Message<JsonObject> message){
        try {
            String path = message.body().getString("path");
            if (path == null || path.isEmpty()) {
                message.fail(400, "Invalid file path");
                return;
            }

            EasyExcel.read(path, SimpleUser.class, new PageReadListener<SimpleUser>(data -> {
                processUsersRecursively(data, message);
            })).sheet().doRead();
        } catch (Exception e) {
            message.fail(500, "Failed to read Excel file: " + e.getMessage());
        }
    }

    /**
     * Process users recursively
     * @author ilyass
     * @param users
     * @param message
     * Processes the list of users recursively
     */
    private void processUsersRecursively(List<SimpleUser> users, Message<JsonObject> message) {
        System.out.println("Processing user at index: " + index);

        if (index < users.size()) {
            SimpleUser user = users.get(index);
            String username = user.getUsername();
            String password = user.getPassword();

            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                errLine++;
                index++;
                processUsersRecursively(users, message);
                return;
            }

            JsonObject body = new JsonObject()
                                .put("username", username)
                                .put("password", password);

            vertx.eventBus().request(Services.USER_CREATE, body, reply -> {
                System.out.println("Request sent for user at index: " + index);

                if (reply.succeeded()) {
                    index++;
                    successLine++;
                } else {
                    index++;
                    errLine++;
                    System.err.println("Error processing user at index " + index + ": " + reply.cause().getMessage());
                }
                processUsersRecursively(users, message);
            });
        } else {
            System.out.println("All users processed, sending success reply. " + successLine + "/" + errLine);
            message.reply(new JsonObject()
                .put("statusCode", 200)
                .put("status", "success")
                .put("totalLines", index)
                .put("successLines", successLine)
                .put("errorLines", errLine)
                .put("message", "Users registered successfully")
            );
        }
    }

}
