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
  }

  /**
   * @param message Message
   * @author : youssef
   * <p>
   * this function is an event bus consumer handler that create notification after manager accepte or reject the demand
   * </p>
   */
  private void createNotificationHandler(Message message) {
    JsonObject body = (JsonObject) message.body();

    String user_id = body.getString(Fields.DEMAND_USER_ID);
    String demand_id = body.getString(Fields.NOTIFICATION_DEMAND_ID);
    String demand_status = body.getString(Fields.DEMAND_STATUS);

    String notification_message;

    if(demand_status.equals("approved")) {
      notification_message = "votre demande a été acceptée";
    }else {
      notification_message = "votre demande a été rejetée";
    }

    JsonObject query = new JsonObject()
      .put(Fields.NOTIFICATION_USER_ID, user_id)
      .put(Fields.NOTIFICATION_DEMAND_ID, demand_id)
      .put(Fields.NOTIFICATION_MESSAGE, notification_message)
      .put(Fields.NOTIFICATION_IS_READ, false)
      .put(Fields.NOTIFICATION_DATE_CREATION, System.currentTimeMillis());

    JsonObject msg = new JsonObject()
      .put("collection" , Collections.NOTIFICATIONS)
      .put("query", query);

    vertx.eventBus().request(Services.DB_INSERT, msg, insertRes -> {
      message.reply(insertRes.result().body());
    });

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

    JsonObject query = body.getJsonObject("query");

    JsonObject msg = new JsonObject().
      put("collection" , Collections.NOTIFICATIONS)
      .put("query" , query);

    vertx.eventBus().request(Services.DB_FIND, msg, res -> {
      if(res.succeeded()) {
        message.reply(res.result().body());
      }else {
        message.reply(res.cause().getMessage());
      }
    });
  }
}
