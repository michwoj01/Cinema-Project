package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.PermissionDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Permission;
import reactor.core.publisher.Flux;

@Service
public class PermissionService {
    @Autowired
    private PermissionDao permissionDao;

    public Flux<Permission> getPermissionsForUser(LoginUser user) {
        return permissionDao.getPermissionsByUserId(user.getId());
    }
}
