package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.cart.CartItem;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    // Gmail SMTP configuration
    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final String DEFAULT_SMTP_PORT = "587";


    private static String SMTP_HOST = DEFAULT_SMTP_HOST;
    private static String SMTP_PORT = DEFAULT_SMTP_PORT;

    // Hardcoded default credentials — can be overridden via configure method
    private static String SENDER_EMAIL = "dovutbekovulukbek@gmail.com";
    private static String SENDER_PASSWORD = "ozxs wfqo jfpm sstx";

    // Public method for configuring email settings at app startup
    public static void configure(String senderEmail, String senderPassword, String smtpHost, String smtpPort) {
        if (senderEmail != null && !senderEmail.isBlank()) SENDER_EMAIL = senderEmail;
        if (senderPassword != null && !senderPassword.isBlank()) SENDER_PASSWORD = senderPassword;
        if (smtpHost != null && !smtpHost.isBlank()) SMTP_HOST = smtpHost;
        if (smtpPort != null && !smtpPort.isBlank()) SMTP_PORT = smtpPort;
    }

    public boolean sendOrderConfirmation(String recipientEmail, Order order) {
        if (SENDER_EMAIL == null || SENDER_EMAIL.isBlank() || SENDER_PASSWORD == null || SENDER_PASSWORD.isBlank()) {
            System.err.println("EmailService not configured: set MAIL_USER and MAIL_PASS environment variables (or EMAIL_USER / EMAIL_PASS) or call EmailService.configure(...) in Main.");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        // Enable JavaMail debug (prints full SMTP dialog)
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Order Confirmation #" + order.getOrderId());

            String emailBody = buildEmailBody(order);
            message.setContent(emailBody, "text/html; charset=utf-8");

            System.out.println("Using SMTP host: " + SMTP_HOST + ", port: " + SMTP_PORT + ", user: " + SENDER_EMAIL + " to send to " + recipientEmail);
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + recipientEmail + ": " + e.getMessage());
            System.err.println("Common causes: wrong MAIL_USER/MAIL_PASS, Gmail requires app-password, network/firewall issues, or SMTP port blocked.");
            e.printStackTrace();
            return false;
        }
    }

    private String buildEmailBody(Order order) {
        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<h2 style='color: #28a745;'>Thank you for your purchase!</h2>");
        body.append("<p>We appreciate your order!</p>");
        body.append("<h3>Order Details:</h3>");
        body.append("<p><strong>Order Number:</strong> ").append(order.getOrderId()).append("</p>");
        body.append("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
        body.append("<tr style='background-color: #f2f2f2;'>");
        body.append("<th>Product</th><th>Quantity</th><th>Price</th><th>Total</th>");
        body.append("</tr>");

        for (CartItem item : order.getItems()) {
            body.append("<tr>");
            body.append("<td>").append(item.getProduct().getName()).append("</td>");
            body.append("<td>").append(item.getQuantity()).append("</td>");
            body.append("<td>").append(String.format("$%.2f", item.getCurrentPrice())).append("</td>");
            body.append("<td>").append(String.format("$%.2f", item.getSubtotal())).append("</td>");
            body.append("</tr>");
        }

        body.append("</table>");
        body.append("<h3 style='color: #28a745;'>Total: ").append(String.format("$%.2f", order.getTotalAmount())).append("</h3>");
        body.append("<p><strong>Payment Method:</strong> ").append(order.getPaymentMethod()).append("</p>");
        body.append("<hr/>");
        body.append("<p style='color: #666;'>Best regards,<br/>E-Commerce Team</p>");
        body.append("</body></html>");

        return body.toString();
    }
}

