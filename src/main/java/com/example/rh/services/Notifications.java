package com.example.rh.services;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Notifications extends AbstractVerticle {

  @Override
  public void start() {
    vertx.eventBus().consumer(Services.NOTIFICATION_CREATE, this::createNotificationHandler);
    vertx.eventBus().consumer(Services.NOTIFICATION_LIST, this::getListNotificationHandler);
    vertx.eventBus().consumer(Services.NOTIFICATION_UPDATE_STATUS, this::updateNotificationStatusHandler);
  }

  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that create notification after manager accepte or reject the demand
   * </p>
   */
  private void createNotificationHandler(Message message) {

    try {
      JsonObject body = (JsonObject) message.body();
      System.out.println(body);

      String demand_id = body.getString(Fields.NOTIFICATION_DEMAND_ID);
      String user_id;
      String notification_message;
      JsonObject query;

      if(!body.containsKey(Fields.NOTIFICATION_EMPLOYEE_ID)){
        user_id = body.getString(Fields.DEMAND_USER_ID);
        String demand_status = body.getString(Fields.DEMAND_STATUS);
        if (demand_status.equals("approved")) {
          notification_message = "votre demande a été acceptée";
        } else {
          notification_message = "votre demande a été rejetée";
        }

        query = new JsonObject()
          .put(Fields.NOTIFICATION_USER_ID, user_id)
          .put(Fields.NOTIFICATION_DEMAND_ID, demand_id)
          .put(Fields.NOTIFICATION_MESSAGE, notification_message)
          .put(Fields.NOTIFICATION_IS_READ, false)
          .put(Fields.NOTIFICATION_DATE_CREATION, System.currentTimeMillis());

      }else {
        user_id = body.getString(Fields.DEMAND_USER_ID);
        String employee = body.getString(Fields.NOTIFICATION_EMPLOYEE_ID);
        String user_username = body.getString(Fields.NOTIFICATION_USER_USERNAME);

        notification_message = "Une nouvelle demande a été créée!";

        query = new JsonObject()
          .put(Fields.NOTIFICATION_USER_ID, user_id)
          .put(Fields.NOTIFICATION_EMPLOYEE_ID, employee)
          .put(Fields.NOTIFICATION_USER_USERNAME, user_username)
          .put(Fields.NOTIFICATION_DEMAND_ID, demand_id)
          .put(Fields.NOTIFICATION_MESSAGE, notification_message)
          .put(Fields.NOTIFICATION_IS_READ, false)
          .put(Fields.NOTIFICATION_DATE_CREATION, System.currentTimeMillis());
      }

      JsonObject msg = new JsonObject()
        .put("collection", Collections.NOTIFICATIONS)
        .put("query", query);

      vertx.eventBus().request(Services.DB_INSERT, msg, insertRes -> {
        message.reply(insertRes.result().body());
      });

    }catch(Exception e) {
      System.out.println("error " + e);
    }
  }

  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that return a list of notification
   * respecting a query and options and return a JsonObject that contains count and docs of notifications
   * </p>
   */
  private void getListNotificationHandler(Message message) {
    JsonObject body = (JsonObject) message.body();

    try {
      JsonObject query = body.getJsonObject("query");
      String user_id = query.getString("user_id");
      JsonObject options = body.getJsonObject("options");

      int page = options.getInteger("page");
      int limit = options.getInteger("limit");
      int skip = (page - 1) * limit;


      JsonArray pipeline = new JsonArray()
        .add(new JsonObject().put("$match" , new JsonObject()
          .put("user_id", user_id)))
        .add(new JsonObject().put("$skip", skip))
        .add(new JsonObject().put("$limit", limit));

      JsonObject aggregation = new JsonObject()
        .put("collection" , Collections.NOTIFICATIONS)
        .put("pipeline", pipeline)
        .put("options", new JsonObject());

      vertx.eventBus().request(Services.DB_AGGREGATE, aggregation, res -> {
        if (res.succeeded()) {
          message.reply(res.result().body());
        } else {
          message.reply(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      System.out.println("error" + e);
    }
  }

  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that update status of notificaton is_read to true
   * </p>
   */
  private void updateNotificationStatusHandler(Message message) {
    JsonObject body = (JsonObject) message.body();

    try {
      String notificationId = body.getString("notification_id");

      JsonObject update = new JsonObject()
        .put(Fields.NOTIFICATION_IS_READ, true);

      JsonObject msg = new JsonObject()
        .put("collection" , Collections.NOTIFICATIONS)
        .put("id" , notificationId)
        .put("update" , update);

      vertx.eventBus().request(Services.DB_UPDATE, msg, res -> {
        if(res.succeeded()){
          message.reply(res.result().body());
        }else {
          message.fail(500, res.cause().getMessage());
        }
      });
    }catch (Exception e) {
      System.out.println("error " + e);
    }
  }

}
