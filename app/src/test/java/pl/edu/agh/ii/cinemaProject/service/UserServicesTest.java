package pl.edu.agh.ii.cinemaProject.service;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.ii.cinemaProject.db.PermissionDao;
import pl.edu.agh.ii.cinemaProject.db.RoleDao;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Permission;
import pl.edu.agh.ii.cinemaProject.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServicesTest {

    @Mock
    private RoleDao roleDao;
    @Mock
    private UserDao userDao;
    @Mock
    private PermissionDao permissionDao;
    @InjectMocks
    private LoginService loginService;
    @InjectMocks
    private RoleService roleService;
    @InjectMocks
    private PermissionService permissionService;

    @Test
    void testLoggingAdmin() {
        LoginUser user = new LoginUser(1, "jan", "kowalski", "admin@admin.pl", 1);
        when(userDao.findByEmail(any())).thenReturn(Mono.empty());
        when(userDao.findByEmail("admin@admin.pl")).thenReturn(Mono.just(user));
        Assertions.assertEquals(user, loginService.login("admin@admin.pl").get());
        Assertions.assertEquals(Either.left("No user found"), loginService.login("admin1@admin.pl"));
    }

    @Test
    void testPermissionForAdmin() {
        LoginUser user = new LoginUser(1, "jan", "kowalski", "admin@admin.pl", 1);
        Permission permission = new Permission(1, "first", true);
        when(permissionDao.getPermissionsByUserId(1)).thenReturn(Flux.just(permission));
        Assertions.assertEquals(permissionService.getPermissionsForUser(user).blockFirst(), permission);
    }

    @Test
    void testGettingRolesFromDb() {
        Role role = new Role(1, "admin", "opis roli");
        when(roleDao.findAll()).thenReturn(Flux.just(role));
        Assertions.assertEquals(roleService.getAllRoles().blockFirst(), role);
    }
}
