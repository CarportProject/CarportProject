package app.observer;

import app.entities.Order;
import app.entities.OrderStatus;
import app.persistence.ConnectionPool;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

import java.util.Objects;

public class SalesEmailObserver implements OrderObserver {

    @Override
    public void update(Order order, OrderStatus status, ConnectionPool connectionPool) {
        GmailEmailSender gmailEmailSender = new GmailEmailSender();

        String baseUrl = System.getenv("BASE_URL") != null
                ? System.getenv("BASE_URL") : "http://localhost:7070";
        String customerName = order.getContactInfo().getFirstName() + " " + order.getContactInfo().getLastName();
        String email = System.getenv("SALES_EMAIL");
        String subject = "";
        String body = "";

        int orderId = order.getId();


        switch (status) {
            case PENDING -> {
                String[] strings = handlePendingStatus(orderId, customerName, baseUrl);
                subject = strings[0];
                body = strings[1];
            }
            case OFFER_SENT, PAID, REJECTED, CANCELLED -> {
            }
        }
        String finalSubject = subject;
        String finalBody = body;
        new Thread(() -> {
            try {
                gmailEmailSender.sendPlainTextEmail(email, finalSubject, finalBody);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }).start();


    }


    private String[] handlePendingStatus(int orderId, String customerName, String baseUrl) {
        String subject = "Ny carport forespørgsel modtaget – QuickByg";
        String body = """
                Hej,
                
                Der er indkommet en ny forespørgsel på en carport.
                
                Ordrenummer: %d
                Kunde: %s
                
                Log ind for at se og behandle forespørgslen:
                %s/admin/orders
                
                Med venlig hilsen
                QuickByg Carport System
                """.formatted(orderId, customerName, baseUrl);
        return new String[]{subject, body};
    }
}
