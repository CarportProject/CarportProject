package app.observer;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialsMapper;
import app.persistence.OrderMapper;
import app.service.PdfService;
import app.service.SvgDrawingService;
import app.util.EmailSender;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.UUID;

public class CustomerEmailObserver implements OrderObserver {
    EmailSender emailSender = new EmailSender();

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
                    String[] strings = handlePendingStatus(customerName, orderId);
                    subject = strings[0];
                    body = strings[1];
                }
                case OFFER_SENT -> {
                    String[] strings = handleOfferSentStatus(customerName, orderId, baseUrl, token);
                    subject = strings[0];
                    body = strings[1];
                }
                case CANCELLED -> {
                    String[] strings = handleCancelledStatus(customerName, orderId);
                    subject = strings[0];
                    body = strings[1];
                }
                case PAID -> {
                    handlePaidStatus(order, customerName, orderId, connectionPool);
                    return;
                }
                case REJECTED -> {
                    String[] strings = handleRejectedStatus(customerName, orderId);
                    subject = strings[0];
                    body = strings[1];
                }
            }
            String finalSubject = subject;
            String finalBody = body;
            new Thread(() -> {
                try {
                    emailSender.sendPlainTextEmail(email, finalSubject, finalBody);
                } catch (MessagingException e) {
                    System.err.println("[CustomerEmailObserver.update] Could not send email to " + email + ": " + e.getMessage());
                }
            }).start();
        } catch (DatabaseException e) {
            System.err.println("[CustomerEmailObserver.update] Something went wrong when trying to access the database " + e.getMessage());
        }
    }

    private String[] handlePendingStatus(String customerName, int orderId) {

        String subject = "Din ordre er modtaget – QuickByg Carport";
        String body = """
                Hej %s,
                
                Tak for din henvendelse! Vi har modtaget din forespørgsel på en carport og vil behandle den hurtigst muligt.
                
                En af vores sælgere vil gennemgå din forespørgsel og vende tilbage med et tilbud.
                
                Ordrenummer: %d
                
                Har du spørgsmål, er du velkommen til at kontakte os.
                
                Med venlig hilsen
                Københavns Erhvervsakademi datamatikerlinjen
                """.formatted(customerName, orderId);
        return new String[]{subject, body};
    }

    private String[] handleOfferSentStatus(String customerName, int orderId, String baseUrl, String token) {

        String subject = "Du har modtaget et tilbud – QuickByg Carport";
        String body = """
                Hej %s,
                
                Vi har gennemgået din forespørgsel og er klar med et tilbud.
                
                Ordrenummer: %d
                
                
                Klik på linket nedenfor for at se og acceptere dit tilbud:
                %s/payment?token=%s
                
                Med venlig hilsen
                Københavns Erhvervsakademi datamatikerlinjen
                """.formatted(customerName, orderId, baseUrl, token);

        return new String[]{subject, body};
    }


    private String[] handleCancelledStatus(String customerName, int orderId) {

        String subject = "Dit tilbud er blevet afvist – QuickByg Carport";
        String body = """
                Hej %s,
                
                Vi har modtaget din afvisning af tilbuddet på din carport.
                
                Ordrenummer: %d
                
                Hvis du fortryder eller ønsker at diskutere et nyt tilbud, er du velkommen til at kontakte os.
                
                Med venlig hilsen
                Københavns Erhvervsakademi datamatikerlinjen
                """.formatted(customerName, orderId);
        return new String[]{subject, body};
    }

    private void handlePaidStatus(Order order, String customerName, int orderId, ConnectionPool connectionPool) {

        String subject = "Betalingsbekræftelse – QuickByg Carport";
        String body = """
                Hej %s,
                
                Tak for din betaling! Vi har modtaget din betaling og din ordre er nu bekræftet.
                
                Ordrenummer: %d
                
                Din stykliste og byggevejledning er vedhæftet denne mail som PDF.
                
                Vi glæder os til at hjælpe dig med din nye carport.
                
                Med venlig hilsen
                Københavns Erhvervsakademi datamatikerlinjen
                """.formatted(customerName, orderId);

        new Thread(() -> {
            try {
                PdfService pdfService = new PdfService();
                String svg = new SvgDrawingService().createFlatRoofWithoutWorkshopSvg(order.getSpecifications()).toString();
                byte[] svgPdf = pdfService.svgToPdf(svg);

                List<MaterialListEntry> entries = new MaterialsMapper().findMaterialListById(orderId, connectionPool);
                byte[] tablePdf = pdfService.tableToPdf(pdfService.buildMaterialListHtml(entries));
                byte[] finalPdf = pdfService.mergePdfs(tablePdf, svgPdf);

                emailSender.sendEmailWithPdf(order.getContactInfo().getEmail(), subject, body, finalPdf, "carport.pdf");
            } catch (Exception e) {
                System.err.println("[CustomerEmailObserver] Kunne ikke generere PDF: " + e.getMessage());
                try {
                    emailSender.sendPlainTextEmail(order.getContactInfo().getEmail(), subject, body);
                } catch (MessagingException ex) {
                    System.err.println("[CustomerEmailObserver] Kunne ikke sende fallback mail: " + ex.getMessage());
                }
            }
        }).start();
    }


    private String[] handleRejectedStatus(String customerName, int orderId) {

        String subject = "Din forespørgsel er blevet afvist – QuickByg Carport";
        String body = """
                Hej %s,
                
                Vi har desværre ikke mulighed for at imødekomme din forespørgsel på nuværende tidspunkt.
                
                Ordrenummer: %d
                
                Hvis du har spørgsmål eller ønsker at afgive en ny forespørgsel, er du velkommen til at kontakte os.
                
                Med venlig hilsen
                Fog Trælast & Byggecenter
                """.formatted(customerName, orderId);
        return new String[]{subject, body};
    }
}
