package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ii.cinemaProject.model.Role;
import reactor.core.publisher.Mono;

@Repository
public interface RoleDao extends ReactiveCrudRepository<Role, Long> {
    Mono<Role> getRoleByName(String name);
}
