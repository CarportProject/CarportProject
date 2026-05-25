package app.observer;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialsMapper;
import app.persistence.OrderMapper;
import app.persistence.SpecificationMapper;
import app.service.PdfService;
import app.service.SvgDrawingService;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.UUID;

public class CustomerEmailObserver implements OrderObserver {
    GmailEmailSender gmailEmailSender = new GmailEmailSender();

    @Override
    public void update(Order order, OrderStatus status, ConnectionPool connectionPool) {
        String email = order.getContactInfo().getEmail();
        String subject = "";
        String body = "";
        String customerName = order.getContactInfo().getFirstName() + " " + order.getContactInfo().getLastName();
        String baseUrl = System.getenv("BASE_URL") != null ?
                System.getenv("BASE_URL") : "http://localhost:7070";

        int orderId = order.getId();
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();


        try {
            OrderMapper.changeOrderDetails(orderId, new OrderDetails(uuid), connectionPool);
            switch (status) {
                case PENDING -> {
                    subject = "Din ordre er modtaget – QuickByg Carport";
                    body = """
                            Hej %s,
                            
                            Tak for din henvendelse! Vi har modtaget din forespørgsel på en carport og vil behandle den hurtigst muligt.
                            
                            En af vores sælgere vil gennemgå din forespørgsel og vende tilbage med et tilbud.
                            
                            Ordrenummer: %d
                            
                            Har du spørgsmål, er du velkommen til at kontakte os.
                            
                            Med venlig hilsen
                            Københavns Erhvervsakademi datamatikerlinjen
                            """.formatted(customerName, orderId);
                }
                case OFFER_SENT -> {
                    subject = "Du har modtaget et tilbud – QuickByg Carport";
                    body = """
                            Hej %s,
                            
                            Vi har gennemgået din forespørgsel og er klar med et tilbud.
                            
                            Ordrenummer: %d
                            
                            
                            Klik på linket nedenfor for at se og acceptere dit tilbud:
                            %s/payment?token=%s
                            
                            Med venlig hilsen
                            Københavns Erhvervsakademi datamatikerlinjen
                            """.formatted(customerName, orderId, baseUrl, token);
                }

                case CANCELLED -> {
                    subject = "Dit tilbud er blevet afvist – QuickByg Carport";
                    body = """
                            Hej %s,
                            
                            Vi har modtaget din afvisning af tilbuddet på din carport.
                            
                            Ordrenummer: %d
                            
                            Hvis du fortryder eller ønsker at diskutere et nyt tilbud, er du velkommen til at kontakte os.
                            
                            Med venlig hilsen
                            Københavns Erhvervsakademi datamatikerlinjen
                            """.formatted(customerName, orderId);
                }
                case PAID -> {
                    subject = "Betalingsbekræftelse – QuickByg Carport";
                    body = """
                            Hej %s,
                            
                            Tak for din betaling! Vi har modtaget din betaling og din ordre er nu bekræftet.
                            
                            Ordrenummer: %d
                            
                            Din stykliste og byggevejledning er vedhæftet denne mail som PDF.
                            
                            Vi glæder os til at hjælpe dig med din nye carport.
                            
                            Med venlig hilsen
                            Københavns Erhvervsakademi datamatikerlinjen
                            """.formatted(customerName, orderId);

                    try {
                        PdfService pdfService = new PdfService();

                        Specifications specs = order.getSpecifications();
                        String svg = new SvgDrawingService().createFlatRoofWithoutWorkshopSvg(specs).toString();
                        byte[] svgPdf = pdfService.svgToPdf(svg);


                        List<MaterialListEntry> entries = new MaterialsMapper().findMaterialListById(orderId, connectionPool);
                        String html = pdfService.buildMaterialListHtml(entries);
                        byte[] tablePdf = pdfService.tableToPdf(html);
                        byte[] finalPdf = pdfService.mergePdfs(tablePdf, svgPdf);

                        gmailEmailSender.sendEmailWithPdf(email, subject, body, finalPdf, "carport.pdf");
                    } catch (Exception e) {
                        System.err.println("[CustomerEmailObserver.update] Kunne ikke generere PDF: " + e.getMessage());
                        gmailEmailSender.sendPlainTextEmail(email, subject, body);
                    }
                    return;
                }
                case REJECTED -> {
                    subject = "Din forespørgsel er blevet afvist – QuickByg Carport";
                    body = """
                            Hej %s,
                            
                            Vi har desværre ikke mulighed for at imødekomme din forespørgsel på nuværende tidspunkt.
                            
                            Ordrenummer: %d
                            
                            Hvis du har spørgsmål eller ønsker at afgive en ny forespørgsel, er du velkommen til at kontakte os.
                            
                            Med venlig hilsen
                            Fog Trælast & Byggecenter
                            """.formatted(customerName, orderId);
                }
            }
            gmailEmailSender.sendPlainTextEmail(email, subject, body);
        } catch (MessagingException e) {
            System.err.println("Could not send to email customer: " + email);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
    private String[] getPendingMessage(String customerName, int orderId){
        String subject;
        String body;
        {
            subject = "Din ordre er modtaget – QuickByg Carport";
            body = """
                            Hej %s,
                            
                            Tak for din henvendelse! Vi har modtaget din forespørgsel på en carport og vil behandle den hurtigst muligt.
                            
                            En af vores sælgere vil gennemgå din forespørgsel og vende tilbage med et tilbud.
                            
                            Ordrenummer: %d
                            
                            Har du spørgsmål, er du velkommen til at kontakte os.
                            
                            Med venlig hilsen
                            Københavns Erhvervsakademi datamatikerlinjen
                            """.formatted(customerName, orderId);
        }
        return  new String[]{subject, body};
    }
}
