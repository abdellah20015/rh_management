package com.example.rh.constants;

public class Services {

  //DB
  public static final String DB_FIND = "find";
  public static final String DB_FIND_ONE = "findOne";
  public static final String DB_FIND_WITH_OPTIONS = "findWithOptions";
  public static final String DB_INSERT = "insert";
  public static final String DB_UPDATE = "update";
  public static final String DB_UPDATE_COLLECTION_WITH_OPTIONS = "updateCollectionWithOptions";
  public static final String DB_REMOVE_DOCUMENT = "removeDocument";
  public static final String DB_REMOVE_DOCUMENTS = "removeDocuments";
  public static final String DB_COUNT = "count";
  public static final String DB_AGGREGATE = "aggregate";



  // Auth
  public static final String AUTH_LOGIN = "login";
  public static final String AUTH_LOGOUT = "logout";
  public static final String AUTH_RESET_PASSWORD = "resetPassword";
  // User
  public static final String USER_CREATE = "createUser";
  public static final String USER_UPDATE = "updateUser";
  public static final String USER_DELETE = "deleteUser";
  public static final String USER_LIST = "listUsers";
  public static final String USER_IMPORT = "importUsers";
  public static final String USER_PROFILE = "getUserProfile";


  //Demands
  public static final String DEMAND_LIST = "getListDemands";
  public static final String DEMAND_CREATE = "createDemand";


  //Contracts
  public static final String CONTRACT_CREATE = "contract.create";
  public static final String CONTRACT_UPDATE = "contract.update";
  public static final String CONTRACT_GET = "contract.get";

  //Files
  public static final String FILE_DOWNLOAD = "file.download";
  public static final String FILE_GET = "file.get";
  public static final String FILE_DOWNLOAD_PDF = "file.download.pdf";

  public static final String DEMAND_UPDATE = "updateDemand";
  public static final String DEMAND_PDF_GENERATE = "generateDemandPdf";

  //notification
  public static final String NOTIFICATION_CREATE = "createNotification";
  public static final String NOTIFICATION_LIST = "getListNotification";
  public static final String NOTIFICATION_UPDATE_STATUS = "updateNotificationStatus";
}
