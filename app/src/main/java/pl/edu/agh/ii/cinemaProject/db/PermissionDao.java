package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Permission;
import reactor.core.publisher.Flux;

public interface PermissionDao extends ReactiveCrudRepository<PermissionDao, Long> {

    @Query("""
            SELECT DISTINCT * FROM permission
            inner join permission_role pr on permission.id = pr.permission_id
            inner join login_user lu on pr.role_id = lu.role_id
            WHERE lu.id = :userId
            """)
    Flux<Permission> getPermissionsByUserId(long userId);
}
