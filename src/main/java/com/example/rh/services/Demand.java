package com.example.rh.services;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class Demand extends AbstractVerticle {

  @Override
  public void start() {
    try {
      //getListDemand service
      vertx.eventBus().consumer(Services.DEMAND_LIST, this::getListDemandHandler);
      vertx.eventBus().consumer(Services.DEMAND_CREATE, this::createDemandHandler);
      vertx.eventBus().consumer(Services.DEMAND_UPDATE, this::updateDemandHandler);
    }catch(Exception e) {
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

      JsonObject query = body.getJsonObject("query", new JsonObject());
      JsonObject msg = new JsonObject()
        .put("query", query)
        .put("collection", Collections.DEMANDS);

      vertx.eventBus().request(Services.DB_FIND, msg, res -> {
        if (res.failed()) {
          message.fail(500, res.cause().getMessage());
          System.out.println(res.cause().getMessage());
        } else {
          message.reply(res.result().body());
        }
      });
    }catch(Exception e) {
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
        .put("query" , query);

      vertx.eventBus().request(Services.DB_INSERT, msg, res -> {
        if(res.succeeded()) {
          System.out.println("demand has been created : " + res.result().body());
          message.reply(res.result().body());
        }else {
          message.reply(res.cause().getMessage());
        }
      });

    }catch(Exception e) {
      message.fail(500, "error" +  e);
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
        if(res.succeeded()){
          message.reply(res.result().body());
        }else {
          message.fail(500, res.cause().getMessage());
        }
      });
    }catch(Exception e) {
      message.fail(500, "error" +  e);
    }
  }
}
