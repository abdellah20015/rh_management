package com.example.rh.services;

import com.example.rh.constants.Fields;
import com.example.rh.constants.Services;
import com.example.rh.constants.StaticFunctions;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PdfGenerator extends AbstractVerticle {

  @Override
  public void start() {
    try {
      //getListDemand service
      vertx.eventBus().consumer(Services.DEMAND_PDF_GENERATE, this::generatePdfHandler);
    }catch(Exception e) {
      System.out.println(e);
    }
  }

  /**
   * @param message Message
   * @author : youssef
   * <p>
   * This function generates a PDF for a demands, adding details like
   * employee name, request type, manager name and other details.
   * </p>
   */
  private void generatePdfHandler(Message message) {
    JsonObject data = new JsonObject(message.body().toString());
    try {
      String pdfFilePath = "uploads/" + data.getString("type") + "_report_" + System.currentTimeMillis() + ".pdf"; ;
      Document document = new Document(PageSize.A4, 60, 60, 50, 50);
      PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
      document.open();
      addContent(document, data);
      document.close();

      message.reply(pdfFilePath);
    }catch (Exception e) {
      System.out.println(e);
    }
  }

  private void addContent(Document document, JsonObject data) throws DocumentException {
    try {
      System.out.println(data);
      Paragraph preface = new Paragraph();

      String demandType = data.getString("type");
      JsonObject details = data.getJsonObject("details");

      JsonObject user = data.getJsonObject("user");
      JsonObject manager = data.getJsonObject("manager");
      JsonObject contract = data.getJsonObject("contract");

      // date
      Long dateInMillis = data.getLong("created_date"); // Use getLong instead of getInteger
      System.out.println("Created Date (in milliseconds): " + dateInMillis);

      //Pdf title
      Map<String, String> demand_details = new HashMap<>();

      String pdfTitle;
      switch (demandType) {
        case "demande_conge":
          pdfTitle = "Demande de congé";
          demand_details.put("manager_name", manager.getString(Fields.USER_PASSWORD));
          demand_details.put("username", user.getString(Fields.USER_USERNAME));
          if(contract.getString(Fields.CONTRACT_DATE_CREATION).equals("CDD")){
            demand_details.put("end_date", Fields.CONTRACT_END_DATE);
          }
          break;
        case "ordre_de_mission":
          pdfTitle = "Ordre de mission";
          demand_details.put("manager_name", manager.getString(Fields.USER_PASSWORD));
          demand_details.put("username", user.getString(Fields.USER_USERNAME));
          demand_details.put("start_date", contract.getString(Fields.CONTRACT_DATE_CREATION));
          if(contract.getString(Fields.CONTRACT_DATE_CREATION).equals("CDD")){
            demand_details.put("end_date", Fields.CONTRACT_END_DATE);
          }
          break;
        default:
          pdfTitle = "Attestation_de_travail";
          demand_details.put("manager_name", manager.getString(Fields.USER_PASSWORD));
          demand_details.put("manager_role", manager.getString(Fields.USER_ROLE));
          demand_details.put("username", user.getString(Fields.USER_USERNAME));
          demand_details.put("contract_type", contract.getString(Fields.CONTRACT_TYPE));
          demand_details.put("start_date", contract.getString(Fields.CONTRACT_DATE_CREATION));
          if(contract.getString(Fields.CONTRACT_DATE_CREATION).equals("CDD")){
            demand_details.put("end_date", Fields.CONTRACT_END_DATE);
          }
          break;
      }

      Paragraph title = new Paragraph(pdfTitle, new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD));
      title.setSpacingAfter(20);


      document.add(title);

      //Pdf details
      preface.setExtraParagraphSpace(10);
      addEmptyLine(preface, 1);
      preface.add(new Paragraph("A: " + data.getString("manager_username"), new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK)));
      preface.add(new Paragraph("de: " + data.getString("username"), new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK)));
      preface.add(new Paragraph("date: " + StaticFunctions.formatDate(dateInMillis, "yyyy-MM-dd"), new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK)));
      addEmptyLine(preface, 2);
      preface.add(new Paragraph(details.getString("description"), new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK)));
      document.add(preface);

    }catch(Exception e) {
      System.out.println("error " + e);
    }

  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }
}
