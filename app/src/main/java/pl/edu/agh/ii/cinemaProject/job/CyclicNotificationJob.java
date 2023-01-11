package pl.edu.agh.ii.cinemaProject.job;

import io.reactivex.rxjava3.core.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.event.FiredPerson;
import pl.edu.agh.ii.cinemaProject.event.StartCyclicCashierNotifiJob;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.service.EmailServiceImpl;
import pl.edu.agh.ii.cinemaProject.service.NotificationService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CyclicNotificationJob {
    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handleFireEvent(FiredPerson event) {
        var firedPerson = (LoginUser) event.getSource();
        System.out.println("Fired person event for " + firedPerson.getFirstName() + " " + firedPerson.getLastName());
        notificationService.getAllUsersToBeNotifiedAboutFired()
                .subscribe(user -> emailService.sendNotificationAboutFired(user.getEmail(), firedPerson));
    }

    @EventListener
    public void startCyclicCashierNotifiJob(StartCyclicCashierNotifiJob event) {
        System.out.println("Starting job");
        new Thread(() -> {
            Observable.interval(15, TimeUnit.SECONDS)
                    .subscribe((i) -> {
                        System.out.println("Stream is on " + i + " iteration");
                        notificationService.getCashierNotificationEmailsAndMessage()
                                .flatMap(pair -> {
                                    List<String> emails = pair.getFirst().stream().map(LoginUser::getEmail).collect(Collectors.toList());
                                    String message = pair.getSecond().getMessage();
                                    emailService.sendRemainderToCashiers(emails, message);
                                    return notificationService.addNotificationLog(pair.getSecond());
                                }).subscribe();
                    });
        }).start();
    }

}
