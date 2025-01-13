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
      Document document = new Document(PageSize.A4, 80, 80, 50, 50);
      PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
      document.open();
      addContent(document, data);
      document.close();

      message.reply(pdfFilePath);
    }catch (Exception e) {
      System.out.println(e);
    }
  }

  private void addContent(Document document, JsonObject data) {
    try {
      String demandType = data.getString("type");

      JsonObject details = data.getJsonObject("details");
      JsonObject user = data.getJsonObject("user");
      JsonObject manager = data.getJsonObject("manager");
      JsonObject contract = data.getJsonObject("contract");

      System.out.println("\n user :"  + user + "\n");
      System.out.println("\n user :"  + manager + "\n");

      // date
      Long dateInMillis = data.getLong("created_date"); // Use getLong instead of getInteger
      System.out.println("Created Date (in milliseconds): " + dateInMillis);

      //Pdf title
      String pdfTitle;
      JsonObject demand_details = new JsonObject();

      switch (demandType) {
        case "demande_conge":
          pdfTitle = "Demande de congé";
          demand_details.put("title", pdfTitle)
            .put("username", user.getString(Fields.USER_USERNAME))
            .put("manager_username", manager.getString(Fields.USER_USERNAME))
            .put("position", details.getString("position"))
            .put("days", details.getString("days"))
            .put("reason", details.getString("reason"))
            .put("start_date", details.getString("start_date"))
            .put("end_date", details.getString("start_date"));
          DemandeDeConge(document, demand_details);
          break;
        case "ordre_de_mission":
          pdfTitle = "Ordre de mission";
          demand_details.put("cin", details.getString(Fields.USER_USERNAME))
            .put("title", pdfTitle)
            .put("username", user.getString(Fields.USER_USERNAME))
            .put("mission_address", details.getString("mission_address"))
            .put("mission_objective", details.getString("mission_objective"))
            .put("transport", details.getString("transport"))
            .put("remuneration", details.getString("remuneration"))
            .put("start_date", details.getString("start_date"))
            .put("end_date", details.getString("start_date"));
          OrderDeMission(document, demand_details);
          break;
        default:
          pdfTitle = "Attestation de travail";
          demand_details.put("manager_username", manager.getString(Fields.USER_USERNAME))
            .put("title", pdfTitle)
            .put("manager_role", manager.getString(Fields.USER_ROLE))
            .put("username", user.getString(Fields.USER_USERNAME))
            .put("contract_type", contract.getString(Fields.CONTRACT_TYPE))
            .put("start_date", contract.getString(Fields.CONTRACT_DATE_CREATION));
          AttestationDeTravail(document, demand_details);
          break;
      }

      System.out.println("---------------------------- \n"+demand_details+"\n");

    }catch(Exception e) {
      System.out.println("error " + e);
    }
  }

  private void DemandeDeConge(Document document, JsonObject congeDetails) throws DocumentException {
    Paragraph preface = new Paragraph();

    // Title
    Paragraph title = new Paragraph(congeDetails.getString("title"), new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD));
    title.setSpacingAfter(50);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    // Body content
    preface.add(createParagraph("À l'attention de :"));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("M./Mme " + congeDetails.getString("manager_username")));
    addEmptyLine(preface, 2);
    preface.add(createParagraph("Je soussigné(e) " + congeDetails.getString("username") + ", occupant le poste de " + congeDetails.getString("position") + ", sollicite un congé de " + congeDetails.getString("days") + " jours du " + congeDetails.getString("start_date") + " au " + congeDetails.getString("end_date") + " inclus."));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Motif : " + congeDetails.getString("reason")));
    addEmptyLine(preface, 1);

    // Conclusion
    preface.add(createParagraph("Je vous prie de bien vouloir prendre en considération ma demande et m'informer de votre décision."));
    addEmptyLine(preface, 2);
    preface.add(createParagraph("Fait le " + StaticFunctions.formatDate(System.currentTimeMillis(), "yyyy-MM-dd") + "."));

    document.add(preface);
  }


  private void AttestationDeTravail(Document document, JsonObject demandDetails) throws DocumentException {
    Paragraph preface = new Paragraph();

    Paragraph title = new Paragraph(demandDetails.getString("title"), new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD));
    title.setSpacingAfter(50);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    preface.add(createParagraph("À qui de droit,"));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Nous, soussignés, " + demandDetails.getString("manager_username") + ", en qualité de " + demandDetails.getString("manager_role") + ", certifions par la présente que :"));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("M./Mme " + demandDetails.getString("username")));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Est employé(e) dans notre société en contrat " + demandDetails.getString("contract_type") + " depuis le " + demandDetails.getString("start_date") + "."));
    preface.add(createParagraph("Cette attestation est délivrée à la demande de " + "l'intéressé(e) pour servir et valoir ce que de droit."));
    addEmptyLine(preface, 2);
    preface.add(createParagraph("Fait le " + StaticFunctions.formatDate(System.currentTimeMillis(), "yyyy-MM-dd") + "."));
    document.add(preface);
  }

  private void OrderDeMission(Document document, JsonObject orderDetails) throws DocumentException {
    Paragraph preface = new Paragraph();

    // Title
    Paragraph title = new Paragraph(orderDetails.getString("title"), new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD));
    title.setSpacingAfter(50);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    // Body content
    preface.add(createParagraph("À l'attention de :"));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("M./Mme " + orderDetails.getString("username")));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Vous êtes désigné(e) pour accomplir la mission suivante :"));
    addEmptyLine(preface, 2);

    // Mission details
    preface.add(createParagraph("Lieu de la mission : " + orderDetails.getString("mission_address")));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Objectif de la mission : " + orderDetails.getString("mission_objective")));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Transport : " + orderDetails.getString("transport")));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Rémunération : " + orderDetails.getString("remuneration")));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Durée de la mission : Du " + orderDetails.getString("start_date") + " au " + orderDetails.getString("end_date")));

    // Conclusion
    addEmptyLine(preface, 2);
    preface.add(createParagraph("Cette mission est une mission temporaire et ne constitue pas une modification de votre contrat de travail."));
    addEmptyLine(preface, 1);
    preface.add(createParagraph("Fait le " + StaticFunctions.formatDate(System.currentTimeMillis(), "yyyy-MM-dd") + "."));

    document.add(preface);
  }



  Paragraph createParagraph(String text) {
    Paragraph p = new Paragraph(text, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK));
    return p;
  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }
}
