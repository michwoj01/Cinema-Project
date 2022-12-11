package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.RoleDao;
import pl.edu.agh.ii.cinemaProject.model.Role;
import reactor.core.publisher.Flux;

@Service
public class RoleService {
    @Autowired
    private RoleDao roleDao;

    public Flux<Role> getAllRoles() {
        return roleDao.findAll();
    }
}
