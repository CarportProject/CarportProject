package app.observer;

import app.entities.Order;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

public class CustomerEmailObserver implements OrderObserver {
    GmailEmailSender gmailEmailSender = new GmailEmailSender();

    @Override
    public void update(Order order, OrderEvent orderEvent) {
        String email = order.getEmail();
        String subject = "";
        String body = "";
        try {
            switch (orderEvent) {
                case ORDER_CREATED -> {
                    subject = "Din ordre er modtaget";
                    //TODO update the body with pdf and relevant info
                    body = "";
                }
                case ORDER_APPROVED -> {
                    subject = "Din ordre er blevet godkendt";
                    //TODO update the body with a payment link
                    body = "";
                }
                case ORDER_CANCELLED -> {
                    subject = "Din ordre er blevet annulleret";
                    //TODO add relevant body to rejection email
                    body = "";
                }
            }

            gmailEmailSender.sendPlainTextEmail(email, subject, body);
        } catch (MessagingException e) {
            System.err.println("Could not send to email customer: " + email);
        }
    }
}
