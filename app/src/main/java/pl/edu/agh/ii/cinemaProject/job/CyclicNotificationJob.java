package pl.edu.agh.ii.cinemaProject.job;

import io.reactivex.rxjava3.core.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.event.FiredPerson;
import pl.edu.agh.ii.cinemaProject.event.StartCyclicCashierNotifyJob;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.service.EmailServiceImpl;
import pl.edu.agh.ii.cinemaProject.service.NotificationService;

import java.util.concurrent.TimeUnit;

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
    public void startCyclicCashierNotifyJob(StartCyclicCashierNotifyJob event) {
        System.out.println("Starting job");
        new Thread(() -> Observable.interval(15, TimeUnit.SECONDS).subscribe(
                i -> notificationService.getCashierNotificationEmailsAndMessage()
                        .flatMap(pair -> {
                            emailService.sendRemainderToCashiers(pair.getFirst(), pair.getSecond().getMessage());
                            return notificationService.addNotificationLog(pair.getSecond());
                        }).subscribe(callback -> System.out.println("Notification complete on " + i + " iteration")),
                e -> System.err.println("Error on notification job"),
                () -> System.out.println("End should never happen")
        )).start();
    }

}
