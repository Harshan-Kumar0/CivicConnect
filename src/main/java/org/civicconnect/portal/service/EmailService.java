package org.civicconnect.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            // ✅ Log every email attempt
            System.out.println("📧 Attempting to send email...");
            System.out.println("➡️ To: " + to);
            System.out.println("➡️ Subject: " + subject);

            // ✅ Prevent empty or null emails from being sent
            if (to == null || to.trim().isEmpty()) {
                System.out.println("⚠️ Email not sent: Recipient address is empty or null.");
                return;
            }

            // ✅ Create message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to.trim());
            message.setSubject(subject);
            message.setText(text);

            // ✅ Send message
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.out.println("❌ Email sending failed:");
            e.printStackTrace(); // shows full cause in console
        }
    }
}
