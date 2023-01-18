package pl.edu.agh.ii.cinemaProject.service;

import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;

import java.io.File;
import java.util.List;

@Service
public class EmailServiceImpl {
    private static final String DOMAIN = "parszywazgraja.pl";
    private static final String FROM_CASHIER_NOTIFICATION = "notify+recommendations@" + DOMAIN;
    private static final String FROM_REPORT_DELIVERY = "send+reports@" + DOMAIN;
    private static final String FROM_FIRED_PERSON = "notify+firedperson@" + DOMAIN;

    @Autowired
    private JavaMailSender emailSender;

    @SneakyThrows
    public void sendRemainderToCashiers(List<String> cashiersEmails, String message) {
        cashiersEmails.forEach(email -> sendMail(message, email, "Cashier remainder", FROM_CASHIER_NOTIFICATION));
    }

    @SneakyThrows
    public void sendReportsToManagers(List<String> managersEmails, File file) {
        managersEmails.forEach(email -> sendReport(file, email));
    }

    public void sendNotificationAboutFired(String email, LoginUser firedPerson) {
        sendMail("You have been notified about firing as he was not " +
                        "a good worker btw. in our company -> " +
                        firedPerson.getFirstName() + " " + firedPerson.getLastName(),
                email, "Fired person", FROM_FIRED_PERSON);
    }

    @SneakyThrows
    private void sendReport(File file, String to) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.addAttachment("report.png", file);
        helper.setFrom(EmailServiceImpl.FROM_REPORT_DELIVERY);
        helper.setTo(to);
        helper.setSubject("Manager Report");
        helper.setText("Report for last few days", false);
        emailSender.send(mimeMessage);
    }

    @SneakyThrows
    private void sendMail(String message, String to, String subject, String from) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);

        System.out.println("Sending email");
        emailSender.send(mimeMessage);
    }
}
