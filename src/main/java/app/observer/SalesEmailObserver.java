package app.observer;

import app.entities.Order;
import app.entities.OrderStatus;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

public class SalesEmailObserver implements OrderObserver {

    @Override
    public void update(Order order, OrderStatus status) {
        GmailEmailSender gmailEmailSender = new GmailEmailSender();
        String email = order.getContactInfo().getEmail();
        String subject = "";
        String body = "";
        try {
            switch (status) {
                case PENDING -> {
                    subject = "Ordre oprettet";

                    //TODO update the body text with relevant message
                    body = "";
                }
            }
            gmailEmailSender.sendPlainTextEmail(email, subject, body);
        } catch (MessagingException e) {
            System.err.println("Could not send sales email to: " + email);
        }
    }
}
