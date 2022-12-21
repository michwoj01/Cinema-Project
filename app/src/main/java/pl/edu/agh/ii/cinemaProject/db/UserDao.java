package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import reactor.core.publisher.Mono;

@Repository
public interface UserDao extends ReactiveCrudRepository<LoginUser, Long> {
    Mono<LoginUser> getLoginUserByEmail(String email);

    Mono<LoginUser> findByEmail(String email);
}
