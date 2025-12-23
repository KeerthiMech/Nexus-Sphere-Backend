package com.authenticator.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromEmail;


    public void sendresetEmail(String email, String passwordResetRequest, String emailBody) {
        String Subject = "Password Reset Request";
        String body = buildEmailResetBody(emailBody);

        sendEmail(email, Subject, body);
    }

    private void sendEmail (String to, String subject, String body) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildEmailResetBody(body), true);

            javaMailSender.send(message);
        }catch(MessagingException e) {
            throw new RuntimeException("Failed to send email", e);

        }
    }

    private String buildEmailResetBody(String emailBody) {
        return """
            <html>
              <body style="font-family: Arial, sans-serif;">
                <h2>Password Reset Request</h2>
                <p>You requested to reset your password.</p>
                <p>Click the link below to set a new password:</p>
                <p>
                  <a href="%s"
                     style="padding:10px 15px;
                            background-color:#4CAF50;
                            color:white;
                            text-decoration:none;">
                     Reset Password
                  </a>
                </p>
                <p>This link will expire in 15 minutes.</p>
                <p>If you did not request this, please ignore this email.</p>
              </body>
            </html>
        """.formatted(emailBody);
    }
}
