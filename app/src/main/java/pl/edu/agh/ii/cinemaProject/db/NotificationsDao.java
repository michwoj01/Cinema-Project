package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Notification;
import reactor.core.publisher.Mono;

public interface NotificationsDao extends ReactiveCrudRepository<Notification, Long> {
    @Query("""
            SELECT n.id, n.name, recomended_movies_func() AS MESSAGE
            FROM
                NOTIFICATION n
            WHERE
                n.name = :name
                and not exists (
                    select *
                    from NOTIFICATIONS_LOG ng
                    where ng.name = n.name and date(ng.SEND_DATE) = current_date
                    )
                    """)
    Mono<Notification> getByNameWithCheckForDuplicateForDayAndFullMessage(String name);

    @Query("""
            SELECT n.*
            FROM
                NOTIFICATION n
            WHERE
                n.name = :name
            """)
    Mono<Notification> getByName(String name);

    @Query("""
            INSERT INTO NOTIFICATIONS_LOG (NAME, MESSAGE, SEND_DATE)
            VALUES (:name, :message, current_timestamp)
            RETURNING ID
            """)
    Mono<Long> addNotificationLog(String name, String message);
}
