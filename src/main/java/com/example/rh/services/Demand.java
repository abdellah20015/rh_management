package com.example.rh.services;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class Demand extends AbstractVerticle {

  private MongoClient mongoClient;
  @Override
  public void start() {
    mongoClient = Conf.createMongoClient(vertx);

    try {
      //getListDemand service
      vertx.eventBus().consumer(Services.DEMAND_LIST_MANAGER, this::getListDemandsByManager);
      vertx.eventBus().consumer(Services.DEMAND_LIST_USER, this::getListDemandsByUser);
      vertx.eventBus().consumer(Services.DEMAND_CREATE, this::createDemandHandler);
      vertx.eventBus().consumer(Services.DEMAND_UPDATE, this::updateDemandHandler);
//      vertx.eventBus().consumer(Services.DEMANDS_REASON, this::demandReason);

    } catch (Exception e) {
      System.out.println(e);
    }
  }


  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that return a list of domands
   * respecting a query and options and return a JsonObject that contains count and docs of domands
   * </p>
   */
  private void getListDemandsByManager(Message message) {
    try {
      JsonObject body = (JsonObject) message.body();
      JsonObject query = body.getJsonObject("query");
      JsonObject filter = query.getJsonObject("filter");
      String search = query.getString("search");
      JsonObject user = body.getJsonObject("user");
      JsonObject option = body.getJsonObject("options");
      int page = option.getInteger("page");
      int limit = option.getInteger("limit");
      int skip = (page - 1) * limit;

      JsonArray pipeline = new JsonArray()
        .add(new JsonObject().put("$lookup", new JsonObject()
          .put("from", "user")
          .put("localField", "user_id")
          .put("foreignField", "_id")
          .put("as", "user")))
        .add(new JsonObject().put("$unwind", "$user"))
        .add(new JsonObject().put("$match", new JsonObject()
          .put("user.manager_id", user.getString("id"))));

      if(search != null){
        pipeline.add(new JsonObject().put("$match", new JsonObject()
          .put("type", new JsonObject()
            .put("$regex", ".*" + search.replace(" ", ".*") + ".*")
            .put("$options", "i"))));
      }

      if(!filter.isEmpty()){
        if(filter.containsKey("type") && !filter.getJsonArray("type").isEmpty()){
          pipeline.add(new JsonObject().put("$match" , new JsonObject()
            .put("type", new JsonObject()
              .put("$in", filter.getJsonArray("type")))));
        }
        if(filter.containsKey("status") && !filter.getJsonArray("status").isEmpty()){
          pipeline.add(new JsonObject().put("$match" , new JsonObject()
            .put("status", new JsonObject()
              .put("$in", filter.getJsonArray("status")))));
        }
      }

      pipeline.add(new JsonObject().put("$skip" , skip))
        .add(new JsonObject().put("$sort", new JsonObject().put("created_date", -1)))
        .add(new JsonObject().put("$limit" , limit))
        .add(new JsonObject().put("$project", new JsonObject()
          .put("_id", 1)
          .put("user_id", 1)
          .put("type", 1)
          .put("details", 1)
          .put("status", 1)
          .put("file_path", 1)
          .put("reason", 1)
          .put("created_date", 1)
          .put("username", "$user.username")));

      JsonObject msg = new JsonObject()
        .put("collection" , Collections.DEMANDS)
        .put("pipeline", pipeline)
        .put("options" , new JsonObject());

      vertx.eventBus().request(Services.DB_AGGREGATE, msg, res -> {
        if (res.failed()) {
          message.fail(500, res.cause().getMessage());
        } else {
          message.reply(res.result().body());
        }
      });
    } catch (Exception e) {
      message.fail(500, "Internal server error: " + e.getMessage());
    }
  }

  private void getListDemandsByUser(Message message) {
    try {
      JsonObject body = (JsonObject) message.body();
      JsonObject query = body.getJsonObject("query");
      JsonObject filter = query.getJsonObject("filter");
      String search = query.getString("search");
      JsonObject user = body.getJsonObject("user");
      String user_id = "";
      if(query.getString("user_id") != null){
        user_id = query.getString("user_id");
        System.out.println(user_id + "this is user id from body");
      }else {
        user_id = user.getString("id");
      }
      JsonObject option = body.getJsonObject("options");
      int page = option.getInteger("page");
      int limit = option.getInteger("limit");
      int skip = (page - 1) * limit;

      JsonArray pipeline = new JsonArray()
        .add(new JsonObject().put("$lookup", new JsonObject()
          .put("from", "user")
          .put("localField", "user_id")
          .put("foreignField", "_id")
          .put("as", "user")))
        .add(new JsonObject().put("$unwind", "$user"))
        .add(new JsonObject().put("$match", new JsonObject()
          .put("user._id", user_id)));

      if(search != null){
        pipeline.add(new JsonObject().put("$match", new JsonObject()
          .put("type", new JsonObject()
            .put("$regex", ".*" + search.replace(" ", ".*") + ".*")
            .put("$options", "i"))));
      }

      if(!filter.isEmpty()){
        if(filter.containsKey("type") && !filter.getJsonArray("type").isEmpty()){
          pipeline.add(new JsonObject().put("$match" , new JsonObject()
            .put("type", new JsonObject()
              .put("$in", filter.getJsonArray("type")))));
        }
        if(filter.containsKey("status") && !filter.getJsonArray("status").isEmpty()){
          pipeline.add(new JsonObject().put("$match" , new JsonObject()
            .put("status", new JsonObject()
              .put("$in", filter.getJsonArray("status")))));
        }
      }

      pipeline.add(new JsonObject().put("$skip" , skip))
        .add(new JsonObject().put("$sort", new JsonObject().put("created_date", -1)))
        .add(new JsonObject().put("$limit" , limit))
        .add(new JsonObject().put("$project", new JsonObject()
          .put("_id", 1)
          .put("user_id", 1)
          .put("type", 1)
          .put("details", 1)
          .put("status", 1)
          .put("file_path", 1)
          .put("reason", 1)
          .put("created_date", 1)
          .put("username", "$user.username")));

      JsonObject msg = new JsonObject()
        .put("collection" , Collections.DEMANDS)
        .put("pipeline", pipeline)
        .put("options" , new JsonObject());

      vertx.eventBus().request(Services.DB_AGGREGATE, msg, res -> {
        if (res.failed()) {
          message.fail(500, res.cause().getMessage());
        } else {
          message.reply(res.result().body());
        }
      });
    } catch (Exception e) {
      message.fail(500, "Internal server error: " + e.getMessage());
    }
  }


  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that create a demand
   * respecting a query and return a JsonObject that contains demand object
   * </p>
   */
  private void createDemandHandler(Message message) {
    try {
      JsonObject body = (JsonObject) message.body();
      System.out.println("message " + body);

      String userId = body.getString(Fields.DEMAND_USER_ID);
      String type = body.getString(Fields.DEMAND_TYPE);
      String status = "pending";
      JsonObject details = body.getJsonObject(Fields.DEMAND_DETAILS);
      String filePath = body.getString(Fields.DEMAND_FILE_PATH);

      JsonObject query = new JsonObject()
        .put(Fields.DEMAND_USER_ID, userId)
        .put(Fields.DEMAND_TYPE, type)
        .put(Fields.DEMAND_DETAILS, details)
        .put(Fields.DEMAND_STATUS, status)
        .put(Fields.DEMAND_FILE_PATH, filePath)
        .put(Fields.DEMAND_DATE_CREATION, System.currentTimeMillis());

      JsonObject msg = new JsonObject()
        .put("collection", Collections.DEMANDS)
        .put("query", query);

      JsonObject contract = new JsonObject()
        .put("collection", Collections.CONTRACTS)
        .put("query", new JsonObject().put(Fields.CONTRACT_USER_ID, userId));

        vertx.eventBus().request(Services.DB_FIND_ONE, contract , reply ->{
          if (reply.succeeded()) {
            JsonObject contractData = (JsonObject) reply.result().body();
            Integer leave_balance = contractData.getInteger(Fields.CONTRACT_LEAVE_BALANCE);
            JsonObject details_ = body.getJsonObject(Fields.DEMAND_DETAILS);
            Integer leave_days = details_.getInteger("days");
            if (type.equals("demande_conge") && leave_balance < leave_days) {
              // if (leave_balance <  leave_days) {
              JsonObject response = new JsonObject()
                .put("status", "error")
                .put("code", 400)
                .put("message", "insufficient leave balance");
              message.reply(response);

              // }
            }
            else{
              vertx.eventBus().request(Services.DB_INSERT, msg, res -> {
                if (res.succeeded()) {

                  JsonObject data = (JsonObject) res.result().body();
                  JsonObject demand = data.getJsonObject("data");

                  JsonArray aggregation = new JsonArray()
                    .add(new JsonObject().put("$lookup", new JsonObject()
                      .put("from", Collections.USER)
                      .put("localField", "user_id")
                      .put("foreignField", "_id")
                      .put("as", "user")))
                    .add(new JsonObject().put("$unwind", new JsonObject()
                      .put("path", "$user")))
                    .add(new JsonObject().put("$lookup", new JsonObject()
                      .put("from", Collections.USER)
                      .put("localField", "user.manager_id")
                      .put("foreignField", "_id")
                      .put("as", "manager")))
                    .add(new JsonObject().put("$unwind", new JsonObject()
                      .put("path", "$manager")))
                    .add(new JsonObject().put("$lookup", new JsonObject()
                      .put("from" , Collections.CONTRACTS)
                      .put("localField", "user._id")
                      .put("foreignField" , "user_id")
                      .put("as" , "contract")))
                    .add(new JsonObject().put("$unwind" , new JsonObject()
                      .put("path" , "$contract")))
                    .add(new JsonObject().put("$match", new JsonObject()
                      .put("_id", demand.getString("_id"))))
                    .add(new JsonObject().put("$project", new JsonObject()
                      .put("type", 1)
                      .put("details", 1)
                      .put("created_date", 1)
                      .put("user", "$user")
                      .put("contract" , "$contract")
                      .put("manager", "$manager")));

                  JsonObject aggregationMsg = new JsonObject()
                    .put("collection", Collections.DEMANDS)
                    .put("pipeline", aggregation)
                    .put("options", new JsonObject());

                  vertx.eventBus().request(Services.DB_AGGREGATE, aggregationMsg, aggregationRes -> {
                    try {
                      if (res.succeeded()) {
                        JsonObject aggregationData = (JsonObject) aggregationRes.result().body();
                        JsonArray dataArray = aggregationData.getJsonArray("data");
                        JsonObject result = dataArray.getJsonObject(0);

                        System.out.println(dataArray);

                        vertx.eventBus().request(Services.DEMAND_PDF_GENERATE, result, generatePdfRes -> {
                          if (generatePdfRes.succeeded()) {

                            JsonObject demandBody = (JsonObject) res.result().body();
                            JsonObject demandData = demandBody.getJsonObject("data");

                            JsonObject update = new JsonObject()
                              .put("file_path", generatePdfRes.result().body());

                            JsonObject updateDemandMsg = new JsonObject()
                              .put("collection", Collections.DEMANDS)
                              .put("id", demandData.getString("_id"))
                              .put("update", update);

                            vertx.eventBus().request(Services.DB_UPDATE, updateDemandMsg, updateDemandRes -> {
                              if (updateDemandRes.succeeded()) {
                                System.out.println("demand has been created : " + updateDemandRes.result().body());

                                //create notification for manager
                                JsonArray pipeline = new JsonArray()
                                  .add(new JsonObject().put("$lookup", new JsonObject()
                                    .put("from" , Collections.USER)
                                    .put("localField", "user_id")
                                    .put("foreignField" , "_id")
                                    .put("as" , "user")))
                                  .add(new JsonObject().put("$unwind" , new JsonObject()
                                    .put("path" , "$user")))
                                  .add(new JsonObject().put("$match" , new JsonObject()
                                    .put("_id" , demand.getString("_id"))))
                                  .add(new JsonObject().put("$project" , new JsonObject()
                                    .put(Fields.NOTIFICATION_USER_USERNAME , "$user.username")
                                    .put(Fields.NOTIFICATION_EMPLOYEE_ID , "$user._id")
                                    .put(Fields.NOTIFICATION_USER_ID , "$user.manager_id")));

                                JsonObject NotificationAggregation = new JsonObject()
                                  .put("collection", Collections.DEMANDS)
                                  .put("pipeline", pipeline)
                                  .put("options", new JsonObject());

                                vertx.eventBus().request(Services.DB_AGGREGATE,NotificationAggregation, aggRes -> {
                                  if(aggRes.succeeded()) {
                                    System.out.println("\n +++ notification agg" + aggRes.result().body());
                                    JsonObject notificationAggRes = (JsonObject) aggRes.result().body();
                                    JsonArray notificationArrayData = notificationAggRes.getJsonArray("data");
                                    JsonObject notificationJsonData = notificationArrayData.getJsonObject(0);

                                    JsonObject notificationData = new JsonObject()
                                      .put(Fields.NOTIFICATION_DEMAND_ID , notificationJsonData.getString("_id"))
                                      .put(Fields.NOTIFICATION_USER_ID, notificationJsonData.getString(Fields.NOTIFICATION_USER_ID))
                                      .put(Fields.NOTIFICATION_USER_USERNAME, notificationJsonData.getString(Fields.NOTIFICATION_USER_USERNAME))
                                      .put(Fields.NOTIFICATION_EMPLOYEE_ID, notificationJsonData.getString(Fields.NOTIFICATION_EMPLOYEE_ID));

                                    vertx.eventBus().request(Services.NOTIFICATION_CREATE, notificationData, notificationRes -> {
                                      message.reply(updateDemandRes.result().body());
                                    });
                                  }
                                });
                              }
                            });
                          } else {
                            message.reply(generatePdfRes.cause().getMessage());
                          }
                        });
                      }
                    }catch (Exception e){
                      System.out.println("error" + e);
                    }
                  });
                } else {
                  message.reply(res.cause().getMessage());
                }
              });

            }
          }
          else{
            message.reply(reply.cause().getMessage());
          }
        });

    } catch (Exception e) {
      message.fail(500, "error" + e);
    }
  }

  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that update demand when it's accepted or rejected by the manager
   * respecting a query and return a JsonObject that contains the updated demand object
   * </p>
   */
  private void updateDemandHandler(Message message) {
    try {
      JsonObject body = (JsonObject) message.body();

      String demandId = body.getString("demand_id");
      String status = body.getString(Fields.DEMAND_STATUS);

      JsonObject update = new JsonObject().
        put(Fields.DEMAND_STATUS, status);

      JsonObject setReasonField = new JsonObject();

      //check if the demand is rejected and add the reason
      if(status.equals("rejected") && body.containsKey(Fields.DEMAND_REASON)){
        String reason = body.getString(Fields.DEMAND_REASON);
        setReasonField.put("$set", new JsonObject()
          .put(Fields.DEMAND_REASON, reason));
      }

      JsonObject msg = new JsonObject()
        .put("collection", Collections.DEMANDS)
        .put("id", demandId)
        .put("update", update);

      vertx.eventBus().request(Services.DB_UPDATE, msg, res -> {
        if (res.succeeded()) {
          try {

            //check if the demand is rejected and add the reason by updating the demand
            if(status.equals("rejected")){
              mongoClient.findOneAndUpdate(Collections.DEMANDS, new JsonObject().put("_id", demandId), setReasonField, resReason -> {
                if (resReason.succeeded()) {
                  System.out.println("reason added" + resReason.result().toString());
                } else {
                  message.reply(resReason.cause().getMessage());
                }
              });
            }

            JsonObject resBody = (JsonObject) res.result().body();
            JsonObject resBodyData = resBody.getJsonObject("data");
            String demand_status = resBodyData.getString(Fields.DEMAND_STATUS);

            String demand_id = resBodyData.getString("_id");
            String user_id = resBodyData.getString(Fields.DEMAND_USER_ID);
            System.out.println("-------------------" + demand_status);
            if (resBodyData.getString(Fields.DEMAND_TYPE).equals("demande_conge") && demand_status.equals("approved")) {
              System.out.println("demande conge approved");
                JsonObject contract = new JsonObject()
                  .put("collection", Collections.CONTRACTS)
                  .put("query", new JsonObject().put(Fields.CONTRACT_USER_ID, user_id));

                vertx.eventBus().request(Services.DB_FIND_ONE, contract, reply ->{
                  if (reply.succeeded()) {

                    JsonObject contractData = (JsonObject) reply.result().body();
                    Integer leave_balance = contractData.getInteger(Fields.CONTRACT_LEAVE_BALANCE);
                    JsonObject details = resBodyData.getJsonObject(Fields.DEMAND_DETAILS);
                    Integer leave_days = details.getInteger("days");
                    Integer new_leave_balance = leave_balance - leave_days;
                    JsonObject contractUpdate = new JsonObject()
                    .put("collection", Collections.CONTRACTS)
                    .put("id",contractData.getString("_id") )
                    .put("update", new JsonObject().put(Fields.CONTRACT_LEAVE_BALANCE, new_leave_balance));
                    if (leave_balance >=  leave_days) {
                      vertx.eventBus().request(Services.DB_UPDATE, contractUpdate, contractUpdateReply -> {
                        if (contractUpdateReply.succeeded()) {
                          System.out.println("leave balance updated");
                        } else {
                          message.reply(contractUpdateReply.cause().getMessage());
                        }
                      });
                    }
                  }
                  else{
                    message.reply(reply.cause().getMessage());
                  }
                });
            }

            JsonObject notification_data = new JsonObject()
              .put(Fields.NOTIFICATION_DEMAND_ID , demand_id)
              .put(Fields.NOTIFICATION_USER_ID, user_id)
              .put(Fields.DEMAND_STATUS, demand_status);

            vertx.eventBus().request(Services.NOTIFICATION_CREATE, notification_data, createNotificationData -> {
              if(createNotificationData.succeeded()) {
                message.reply(res.result().body());
              }else {
                message.reply(createNotificationData.cause());
              }
            });
          }catch (Exception e) {
            System.out.println(e.getMessage());
          }
        } else {
          message.fail(500, res.cause().getMessage());
        }
      });
    } catch (Exception e) {
      message.fail(500, "error" + e);
    }
  }

//  private void demandReason(Message message) {
//    JsonObject body = (JsonObject) message.body();
//
//    String demandId = body.getString("demand_id");
//    String reason = body.getString(Fields.DEMAND_REASON);
//
//    JsonObject update = new JsonObject()
//      .put("$set" , new JsonObject()
//        .put(Fields.DEMAND_REASON, reason));
//
//    JsonObject msg = new JsonObject()
//      .put("collection", Collections.DEMANDS )
//      .put("id", demandId)
//      .put("update", update);
//
//    vert
//  }

}
