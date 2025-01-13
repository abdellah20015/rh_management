package com.example.rh.services;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Demand extends AbstractVerticle {

  @Override
  public void start() {
    try {
      //getListDemand service
      vertx.eventBus().consumer(Services.DEMAND_LIST, this::getListDemandHandler);
      vertx.eventBus().consumer(Services.DEMAND_CREATE, this::createDemandHandler);
      vertx.eventBus().consumer(Services.DEMAND_UPDATE, this::updateDemandHandler);
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
  private void getListDemandHandler(Message message) {
    try {
      JsonObject body = (JsonObject) message.body();
      System.out.println("message " + body);
      String manager_id = body.getJsonObject("query").getString("manager_id");

      JsonArray pipeline = new JsonArray()
        .add(new JsonObject().put("$lookup", new JsonObject()
          .put("from", "user")
          .put("localField", "user_id")
          .put("foreignField", "_id")
          .put("as", "user")))
        .add(new JsonObject().put("$unwind", "$user"))
        .add(new JsonObject().put("$match", new JsonObject()
          .put("user.manager_id", manager_id)))
        .add(new JsonObject().put("$project", new JsonObject()
          .put("user", 0)));

      System.out.println("message " + pipeline);

      JsonObject msg = new JsonObject()
        .put("collection" , Collections.DEMANDS)
        .put("pipeline", pipeline)
        .put("options" , new JsonObject());

      vertx.eventBus().request(Services.DB_AGGREGATE, msg, res -> {
        if (res.failed()) {
          message.fail(500, res.cause().getMessage());
          System.out.println(res.cause().getMessage());
        } else {
          message.reply(res.result().body());
        }
      });
    } catch (Exception e) {
      System.out.println(e);
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
      String status = "padding";
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
            .add(new JsonObject().put("$lookup", new JsonObject()
              .put("from" , Collections.CONTRACTS)
              .put("localField", "user._id")
              .put("foreignField" , "user_id")
              .put("as" , "contract")))
            .add(new JsonObject().put("$unwind", new JsonObject()
              .put("path", "$manager")))
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
                        message.reply(updateDemandRes.result().body());
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

      JsonObject update = new JsonObject()
        .put(Fields.DEMAND_STATUS, status);

      JsonObject msg = new JsonObject()
        .put("collection", Collections.DEMANDS)
        .put("id", demandId)
        .put("update", update);

      vertx.eventBus().request(Services.DB_UPDATE, msg, res -> {
        if (res.succeeded()) {

          JsonObject resBody = (JsonObject) res.result().body();
          JsonObject resBodyData = resBody.getJsonObject("data");

          String demand_id = resBodyData.getString("_id");
          String demand_status = resBodyData.getString(Fields.DEMAND_STATUS);
          String user_id = resBodyData.getString(Fields.DEMAND_USER_ID);

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

        } else {
          message.fail(500, res.cause().getMessage());
        }
      });
    } catch (Exception e) {
      message.fail(500, "error" + e);
    }
  }
}
