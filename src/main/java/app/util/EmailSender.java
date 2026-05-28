package app.util;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.Properties;

public class EmailSender {
    private final String username;
    private final String apiKey;

    public EmailSender() {
        this.username = System.getenv("MAIL_USERNAME");
        this.apiKey = System.getenv("SENDGRID_API_KEY");

        if (username == null || apiKey == null) {
            throw new IllegalStateException("MAIL_USERNAME and SENDGRID_API_KEY environment variables must be set.");
        }
    }

    public void sendPlainTextEmail(String to, String subject, String body) throws MessagingException {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("apikey", apiKey);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body); // Plain text only

        Transport.send(message);
        System.out.println("Email sent successfully to " + to);
    }

    public void sendEmailWithPdf(String to, String subject, String body, byte[] pdfBytes, String fileName) throws MessagingException{
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); //TLS
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("apikey", apiKey);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        MimeBodyPart pdfPart = new MimeBodyPart();
        pdfPart.setDataHandler(new DataHandler(new ByteArrayDataSource(pdfBytes, "application/pdf")));
        pdfPart.setFileName(fileName);


        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(pdfPart);

        message.setContent(multipart);
        Transport.send(message);

        System.out.println("Email med PDF sendt til " + to);
    }

    // 🧪 Main-metode til test
    public static void main(String[] args) {
        EmailSender sender = new EmailSender();

        String to = "recipient@example.com";  // Erstat med din modtager
        String subject = "Testmail fra Java";
        String body = "Hej! Dette er en simpel testmail sendt med Java og Jakarta Mail.";

        try {
            sender.sendPlainTextEmail(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}