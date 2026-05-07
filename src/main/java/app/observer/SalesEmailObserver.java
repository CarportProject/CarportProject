package app.observer;

import app.entities.Order;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

public class SalesEmailObserver implements OrderObserver {

    @Override
    public void update(Order order, OrderEvent event) {
        GmailEmailSender gmailEmailSender = new GmailEmailSender();
        String email = order.getEmail();
        String subject = "";
        String body = "";
        try {
            switch (event) {
                case ORDER_CREATED -> {
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
