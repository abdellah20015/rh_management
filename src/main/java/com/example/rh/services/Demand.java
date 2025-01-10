package com.example.rh.services;

import com.example.rh.constants.Collections;
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
      System.out.println(body);

      JsonObject query = body.getJsonObject("query", new JsonObject());
      JsonObject msg = new JsonObject()
        .put("query", query)
        .put("collection", Collections.DEMANDS);

      vertx.eventBus().request(Services.DB_FIND, msg, hdlr -> {
        if (hdlr.failed()) {
          message.fail(500, hdlr.cause().getMessage());
          System.out.println(hdlr.cause().getMessage());
        } else {
          message.reply(hdlr.result().body());
        }
      });
    }catch(Exception e) {
      System.out.println(e);
    }
  }
}
