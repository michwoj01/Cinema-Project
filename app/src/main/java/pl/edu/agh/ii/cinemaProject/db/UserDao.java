package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserDao extends ReactiveCrudRepository<LoginUser, Long> {
    @Query("SELECT l.email FROM LOGIN_USER l WHERE l.role_id IN (SELECT r.id FROM ROLE r where r.name = 'kasjer')")
    Flux<String> findAllCashiers();

    Mono<LoginUser> findByEmail(String email);
}
