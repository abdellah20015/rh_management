package com.example.rh.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.rh.constants.Collections;
import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Contract extends AbstractVerticle {

  @Override
  public void start() {
    try {

      vertx.eventBus().consumer(Services.CONTRACT_CREATE, this::createContractHandler);
      vertx.eventBus().consumer(Services.CONTRACT_UPDATE, this::updateContractHandler);
      vertx.eventBus().consumer(Services.CONTRACT_GET, this::getContractHandler);

      vertx.setPeriodic(10 * 1000, id -> checkAndExpireContracts());
    } catch(Exception e) {
      System.out.println(e);
    }
  }

/**
 * @author : abdellah
 * Vérifie les contrats CDD expirés et désactive les contrats et les comptes des utilisateurs concernés.
 */


 private void checkAndExpireContracts() {
  System.out.println("Checking for expired contracts...");
  String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
  System.out.println("Current date: " + currentDate);

  JsonObject query = new JsonObject()
    .put("type", "cdd")
    .put("status", true)
    .put("end_date", currentDate);

  JsonObject msg = new JsonObject()
    .put("collection", Collections.CONTRACTS)
    .put("query", query);

  System.out.println("Query for expired contracts: " + query.encodePrettily());

  vertx.eventBus().request(Services.DB_FIND, msg, res -> {
    if (res.succeeded()) {
      JsonObject response = (JsonObject) res.result().body();

      if (response.getString("status").equals("success")) {
        JsonArray contractsData = response.getJsonArray("data");
        if (contractsData != null && !contractsData.isEmpty()) {
          System.out.println("Found " + contractsData.size() + " contracts to expire");
          processExpiredContracts(contractsData);
        } else {
          System.out.println("No contracts to expire");
        }
      } else {
        System.err.println("Query failed with status: " + response.getString("status"));
      }
    } else {
      System.err.println("Error querying expired contracts: " + res.cause().getMessage());
    }
  });
}
/**
 * @param contracts JsonArray
 * @author : abdellah
 * <p>
 * Traite les contrats expirés en fonction de la date de fin et désactive les utilisateurs concernés.
 * </p>
 */

private void processExpiredContracts(JsonArray contracts) {
  String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

  for (int i = 0; i < contracts.size(); i++) {
    JsonObject contract = contracts.getJsonObject(i);
    String contractId = contract.getString("_id");
    String userId = contract.getString("user_id");
    String endDate = contract.getString("end_date");

    if (contractId == null || userId == null || endDate == null) {
      System.err.println("Invalid contract data: " + contract.encodePrettily());
      continue;
    }

    System.out.println("Processing contract: " + contractId + " for user: " + userId + " ending: " + endDate);

    if (currentDate.equals(endDate)) {
      System.out.println("Expiring contract " + contractId + " for user: " + userId);

      JsonObject updateContract = new JsonObject()
        .put("collection", Collections.CONTRACTS)
        .put("id", contractId)
        .put("update", new JsonObject().put("status", false));

      vertx.eventBus().request(Services.DB_UPDATE, updateContract, updateRes -> {
        if (updateRes.succeeded()) {
          System.out.println("Contract " + contractId + " expired successfully");
          deactivateUser(userId);
        } else {
          System.err.println("Failed to expire contract " + contractId + ": " + updateRes.cause().getMessage());
        }
      });
    } else {
      System.out.println("Contract " + contractId + " is not expiring today");
    }
  }
}
/**
   * @param userId String
   * @author : abdellah
   * <p>
   * this function is an deactive user
   * </p>
   */

private void deactivateUser(String userId) {
  JsonObject updateUser = new JsonObject()
    .put("collection", Collections.USER)
    .put("id", userId)
    .put("update", new JsonObject().put("status", false));

  vertx.eventBus().request(Services.DB_UPDATE, updateUser, res -> {
    if (res.succeeded()) {
      System.out.println("User " + userId + " deactivated successfully");
    } else {
      System.err.println("Failed to deactivate user: " + userId);
    }
  });
}

/**
   * @param userId String
   * @author : abdellah
   * <p>
   * this function is an active user
   * </p>
   */

private void activateUser(String userId) {
  JsonObject updateUser = new JsonObject()
    .put("collection", Collections.USER)
    .put("id", userId)
    .put("update", new JsonObject().put("status", true));

  vertx.eventBus().request(Services.DB_UPDATE, updateUser, res -> {
    if (!res.succeeded()) {
      System.err.println("Failed to activate user: " + userId);
    }
  });
}

/**
   * @param message Message
   * @author : abdellah
   * <p>
   * this function is an event bus consumer handler that create a contract
   * </p>
   */

private void createContractHandler(Message<JsonObject> message) {
  try {
    JsonObject body = (JsonObject) message.body();

    if (!validateContractFields(body)) {
      message.fail(400, "Champs requis manquants");
      return;
    }

    JsonObject contract = new JsonObject()
      .put("user_id", body.getString("user_id"))
      .put("type", body.getString("type"))
      .put("start_date", body.getString("start_date"))
      .put("status", body.getBoolean("status"))
      .put("salary", body.getDouble("salary"))
      .put("leave_balance", body.getInteger("leave_balance"))
      .put("date_creation", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

    if (body.containsKey("end_date")) {
      contract.put("end_date", body.getString("end_date"));
    }

    JsonObject msg = new JsonObject()
      .put("collection", Collections.CONTRACTS)
      .put("query", contract);

    vertx.eventBus().request(Services.DB_INSERT, msg, res -> {
      if (res.succeeded()) {
        activateUser(body.getString("user_id"));
        message.reply(res.result().body());
      } else {
        message.fail(500, res.cause().getMessage());
      }
    });
  } catch(Exception e) {
    System.err.println("Error in createContractHandler: " + e);
    message.fail(500, "Unexpected error: " + e.getMessage());
  }
}

  /**
   * @param message Message
   * @author : abdellah
   * <p>
   * this function is an event bus consumer handler that updates a contract
   * respecting the provided fields and returns a JsonObject containing the updated contract
   * </p>
   */
  private void updateContractHandler(Message<JsonObject> message) {
    try {
      JsonObject body = (JsonObject) message.body();

      String contractId = body.getString("contract_id");
      if (contractId == null || contractId.isEmpty()) {
        message.fail(400, "ID du contrat requis");
        return;
      }

      JsonObject updateFields = new JsonObject();
      if (body.containsKey("type")) updateFields.put(Fields.CONTRACT_TYPE, body.getString("type"));
      if (body.containsKey("start_date")) updateFields.put(Fields.CONTRACT_START_DATE, body.getString("start_date"));
      if (body.containsKey("end_date")) updateFields.put(Fields.CONTRACT_END_DATE, body.getString("end_date"));
      if (body.containsKey("status")) updateFields.put(Fields.CONTRACT_STATUS, body.getBoolean("status"));
      if (body.containsKey("salary")) updateFields.put(Fields.CONTRACT_SALARY, body.getDouble("salary"));
      if (body.containsKey("leave_balance")) updateFields.put(Fields.CONTRACT_LEAVE_BALANCE, body.getInteger("leave_balance"));

      JsonObject msg = new JsonObject()
        .put("collection", Collections.CONTRACTS)
        .put("id", contractId)
        .put("update", updateFields);

      vertx.eventBus().request(Services.DB_UPDATE, msg, res -> {
        if (res.succeeded()) {
          message.reply(res.result().body());
        } else {
          message.fail(500, res.cause().getMessage());
        }
      });
    } catch(Exception e) {
      message.fail(500, "error " + e);
    }
  }

  /**
   * @param message Message
   * @author : abdellah
   * <p>
   * this function is an event bus consumer handler that retrieves a contract
   * by its ID and returns a JsonObject containing the contract details
   * </p>
   */
  private void getContractHandler(Message<JsonObject> message) {
    try {
      JsonObject body = (JsonObject) message.body();

      String contractId = body.getString("contract_id");
      if (contractId == null || contractId.isEmpty()) {
        message.fail(400, "ID du contrat requis");
        return;
      }

      JsonObject query = new JsonObject().put("_id", contractId);
      JsonObject msg = new JsonObject()
        .put("collection", Collections.CONTRACTS)
        .put("query", query);

      vertx.eventBus().request(Services.DB_FIND_ONE, msg, res -> {
        if (res.succeeded()) {
          message.reply(res.result().body());
        } else {
          message.fail(500, res.cause().getMessage());
        }
      });
    } catch(Exception e) {
      message.fail(500, "error " + e);
    }
  }

  /**
   * @param contract JsonObject
   * @author : abdellah
   * <p>
   * Checks if the given JSON object contains all required contract fields.
   * </p>
   */
  private boolean validateContractFields(JsonObject contract) {
    return contract.containsKey("user_id") &&
           contract.containsKey("type") &&
           contract.containsKey("start_date") &&
           contract.containsKey("status") &&
           contract.containsKey("salary") &&
           contract.containsKey("leave_balance");
  }
}
