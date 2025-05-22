package com.thathsara.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailsSender;
    
    public void sendNotificationEmail(String toEmail, String subject, String title, String messageBody) {
        try {
            final MimeMessage message = mailsSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildNotificationEmailHtml(title, messageBody));
            mailsSender.send(message);
            
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send Email"+ e.getMessage());
        }
    }

    private String buildNotificationEmailHtml(String title, String messageBody) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <meta charset="UTF-8">
            <title>Notification</title>
            <style>
                body { 
                    margin:0; 
                    padding:0; 
                    background-color:#f3f4f6; 
                    font-family:'Segoe UI', sans-serif; 
                    color:#374151;
                }
                .container {
                    max-width:480px;
                    margin:30px auto;
                    background-color:#fff;
                    border-radius:12px;
                    overflow:hidden;
                    box-shadow:0 4px 12px rgba(0,0,0,0.08);
                }
                .header {
                    background-color:#1e40af;
                    padding:24px;
                    text-align:center;
                    color:#fff;
                }
                .header h1 {
                    margin:0;
                    font-size:24px;
                }
                .body {
                    padding:24px;
                    text-align:center;
                }
                .body h2 {
                    color:#111827;
                    font-size:20px;
                    margin-bottom:12px;
                }
                .body p {
                    font-size:16px;
                    color:#374151;
                    margin:12px 0;
                }
                .footer {
                    background-color:#f9fafb;
                    padding:16px;
                    text-align:center;
                    font-size:12px;
                    color:#9ca3af;
                }
                .footer a {
                    color:#3b82f6;
                    text-decoration:none;
                    font-weight:500;
                }
            </style>
            </head>
            <body>
            <div class="container">
                <div class="header">
                <h1>📬 StormGate Notification</h1>
                </div>
                <div class="body">
                <h2>""" + title + "</h2>\n"
                + "  <p>" + messageBody + "</p>\n"
                + "</div>\n"
                + "<div class=\"footer\">\n"
                + "  If you have any questions, feel free to\n"
                + "  <a href=\"mailto:support@stormgate.io\">contact us</a>.<br/>\n"
                + "  © 2025 StormGate Inc.\n"
                + "</div>\n"
                + "</div>\n"
                + "</body>\n"
                + "</html>";
    }
}
