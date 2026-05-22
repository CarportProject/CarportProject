package app.observer;

import app.entities.Order;
import app.entities.OrderStatus;
import app.persistence.ConnectionPool;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

import java.util.Objects;

public class SalesEmailObserver implements OrderObserver {

    @Override
    public void update(Order order, OrderStatus status) throws MessagingException {
        GmailEmailSender gmailEmailSender = new GmailEmailSender();

        String baseUrl = System.getenv("BASE_URL") != null
                ? System.getenv("BASE_URL") : "http://localhost:7070";
        String customerName = order.getContactInfo().getFirstName() + " " + order.getContactInfo().getLastName();
        String email = System.getenv("SALES_EMAIL");
        String subject = "";
        String body = "";

        int orderId = order.getId();

        try {
            if (Objects.requireNonNull(status) == OrderStatus.PENDING) {
                subject = "Ny carport forespørgsel modtaget – QuickByg";
                body = """
            Hej,
            
            Der er indkommet en ny forespørgsel på en carport.
            
            Ordrenummer: %d
            Kunde: %s
            
            Log ind for at se og behandle forespørgslen:
            %s/admin/orders
            
            Med venlig hilsen
            QuickByg Carport System
            """.formatted(orderId, customerName, baseUrl);
            }
            gmailEmailSender.sendPlainTextEmail(email, subject, body);
        } catch (MessagingException e) {
            System.err.println("[SalesEmailObserver.update] Could not send sales email to: " + email);
            throw e;
        }
    }
}
