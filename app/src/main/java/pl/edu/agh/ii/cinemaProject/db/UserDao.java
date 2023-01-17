package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserDao extends ReactiveCrudRepository<LoginUser, Long> {

    @Query("""
            SELECT l.email from login_user l
            inner join role r on r.id = l.role_id
            where r.name = :name
            """)
    Flux<String> findAllByRoleName(String name);

    Mono<LoginUser> findByEmail(String email);
}
