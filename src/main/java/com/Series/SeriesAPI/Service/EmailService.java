package com.Series.SeriesAPI.Service;

import com.Series.SeriesAPI.DTO.MailBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Value("${spring.mail.username}")
    String senderMail;

    public void sendSimpleMessage(MailBody mailBody){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom(senderMail);
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        // we pass in the object created above into javaMailSender object
        javaMailSender.send(message);

    }
}
