package com.example.rh.services;

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

    Paragraph preface = new Paragraph();

    String demandType = data.getString("type");
    JsonObject details = data.getJsonObject("details");

    // date
    Long dateInMillis = data.getLong("created_date"); // Use getLong instead of getInteger
    System.out.println("Created Date (in milliseconds): " + dateInMillis);

    //Pdf title
    Paragraph title = new Paragraph(demandType, new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD));
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

  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }
}
