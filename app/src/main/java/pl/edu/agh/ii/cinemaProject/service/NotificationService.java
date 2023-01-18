package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.NotificationsDao;
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
    private NotificationsDao notificationsDao;

    public Mono<Pair<List<String>, Notification>> getCashierNotificationEmailsAndMessage() {
        return notificationsDao.getByNameWithCheckForDuplicateForDayAndFullMessage(cashierNotificationName)
                .flatMap(notification -> userDao.findAllByRoleName("kasjer").collectList().map(cashiers -> Pair.of(cashiers, notification)));
    }

    public Mono<Long> addNotificationLog(Notification notification) {
        return notificationsDao.addNotificationLog(notification.getName(), notification.getMessage());
    }

    public Mono<Notification> getCashierNotification() {
        return notificationsDao.getByName(cashierNotificationName);
    }

    public Flux<LoginUser> getAllUsersToBeNotifiedAboutFired() {
        return userDao.findAll();
    }

    public Mono<Notification> saveNotification(Notification notification) {
        return notificationsDao.save(notification);
    }
}
