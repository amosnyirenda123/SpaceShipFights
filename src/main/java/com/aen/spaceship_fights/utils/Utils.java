package com.aen.spaceship_fights.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import java.io.InputStream;
import java.io.IOException;

public class Utils {
    public String generateVerificationToken(){
        Random rand = new Random();
        return Integer.toString(rand.nextInt(900000) + 100000);
    }

    public Timestamp getTimestamp(int minutes){
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime newDateTime = currentDateTime.plusMinutes(minutes);
        return Timestamp.valueOf(newDateTime);
    }

    public static String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }

    public void sendEmail(String toEmail, String subject, String body) {


        Properties configProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("env.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }


            configProperties.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }


        String fromEmail = configProperties.getProperty("email.from");
        String password = configProperties.getProperty("email.password");

        if (fromEmail == null || password == null) {
            System.out.println("Missing email credentials in config.properties");
            return;
        }


        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", "smtp.gmail.com");
        mailProperties.put("mail.smtp.port", "587");
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", "true");


        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {

            MimeMessage message = new MimeMessage(session);


            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(body);


            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
