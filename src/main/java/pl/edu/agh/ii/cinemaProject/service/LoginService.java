package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserDao userDao;

    public Optional<LoginUser> login(String email) {
        return userDao
                .getLoginUserByEmail(email).blockOptional();
    }
}
