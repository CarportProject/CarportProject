package app.observer;

import app.entities.Order;
import app.entities.OrderStatus;
import app.util.GmailEmailSender;
import jakarta.mail.MessagingException;

public class CustomerEmailObserver implements OrderObserver {
    GmailEmailSender gmailEmailSender = new GmailEmailSender();

    @Override
    public void update(Order order, OrderStatus status) {
        String email = order.getContactInfo().getEmail();
        String subject = "";
        String body = "";
        try {
            switch (status) {
                case PENDING -> {
                    subject = "Din ordre er modtaget";
                    //TODO update the body with pdf and relevant info
                    body = "";
                }
                case OFFER_SENT -> {
                    subject = "Din ordre er blevet godkendt";
                    //TODO update the body with a payment link
                    body = "";
                }
                case CANCELLED -> {
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
