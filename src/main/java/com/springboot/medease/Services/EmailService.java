package com.springboot.medease.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) throws MailException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Login OTP");
        message.setText("Your OTP is: " + otp + "\nThis OTP expires in 2 minutes.");

        mailSender.send(message);
    }
}

