package pl.edu.agh.ii.cinemaProject.service;

import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;

import java.util.List;

@Service
public class EmailServiceImpl {
    private static final String DOMAIN = "parszywazgraja.pl";
    private static final String FROM_CASHIER_NOTIFICATION = "notify+recomendations@" + DOMAIN;
    private static final String FROM_FIRED_PERSON = "notify+firedperson@" + DOMAIN;
    @Autowired
    private JavaMailSender emailSender;

    @SneakyThrows
    public void sendRemainderToCashiers(List<String> cashiersEmails, String message) {
        for (String email : cashiersEmails) {
            sendMail(message, email, "Cashier remainder", FROM_CASHIER_NOTIFICATION);
        }
    }

    @SneakyThrows
    private void sendMail(String message, String to, String subject, String from) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);

        System.out.println("Sending email");
        emailSender.send(mimeMessage);
    }

    public void sendNotificationAboutFired(String email, LoginUser firedPerson) {
        sendMail("You have been notified about firing as he was not a good worker btw. in our company -> " + firedPerson.getFirstName() + " " + firedPerson.getLastName(), email, "Fired person", FROM_FIRED_PERSON);
    }
}