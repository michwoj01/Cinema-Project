package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserDao extends ReactiveCrudRepository<LoginUser, Long> {
    Mono<LoginUser> getLoginUserByEmail(String email);

    Mono<LoginUser> findByEmail(String email);

    @Query("SELECT * FROM LOGIN_USER WHERE role_id IN (SELECT r.id FROM ROLE r where r.name = 'kasjer')")
    Flux<LoginUser> findAllCashiers();

    @Query("SELECT * FROM LOGIN_USER")
    Flux<LoginUser> findAllToBeNotifiedAboutFiring();
}
