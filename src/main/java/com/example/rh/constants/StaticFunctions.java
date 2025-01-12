package com.example.rh.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaticFunctions {
  public static String formatDate(long millis,String format){
    try {
      Date date = new Date(millis);
      DateFormat dateFormat = new SimpleDateFormat(format);
      return dateFormat.format(date);
    }catch (Exception e){
      return "";
    }
  }
}
