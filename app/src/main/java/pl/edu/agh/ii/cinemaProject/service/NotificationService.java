package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.NotificationDao;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class NotificationService {
    private final String cashierNotificationName = "Cashier Notification";
    @Autowired
    private UserDao userDao;
    @Autowired
    private NotificationDao notificationDao;

    public Mono<Pair<List<LoginUser>, Notification>> getCashierNotificationEmailsAndMessage() {
        return notificationDao.getByName(cashierNotificationName)
                .flatMap(notification -> userDao.findAllCashiers().collectList()
                        .map(cashiers -> Pair.of(cashiers, notification)));
    }

    public Mono<Long> addNotificationLog(Notification notification) {
        return notificationDao.addNotificationLog(notification.getName(), notification.getMessage());
    }

    public Flux<LoginUser> getAllUsersToBeNotifiedAboutFired() {
        return userDao.findAllToBeNotifiedAboutFiring();
    }
}
