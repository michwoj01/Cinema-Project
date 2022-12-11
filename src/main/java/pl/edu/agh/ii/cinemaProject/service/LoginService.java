package pl.edu.agh.ii.cinemaProject.service;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Role;

import java.util.regex.Pattern;

@Service
public class LoginService {

    private final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @Autowired
    private UserDao userDao;

    private Either<String, ?> checkEmail(String email) {
        if (!emailPattern.matcher(email).matches()) {
            return Either.left("Invalid email");
        } else {
            return Either.right(Option.none());
        }
    }

    public Either<String, LoginUser> createUser(String firstName, String lastName, String email, Role role) {
        return checkEmail(email).flatMap((unused) -> {
            var userExisting = userDao.findByEmail(email).blockOptional();
            if (userExisting.isPresent()) {
                return Either.left("User with this email already exists");
            } else {
                return Either.right(Option.none());
            }
        }).flatMap((unused) -> {
            LoginUser user = new LoginUser();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setRoleId(role.getId());

            var returningUser = userDao.save(user).blockOptional();
            return returningUser.<Either<String, LoginUser>>map(Either::right).orElseGet(() -> Either.left("Unknown error while creating user"));
        });
    }

    public Either<String, LoginUser> login(String email) {
        return checkEmail(email)
                .flatMap((unused) ->
                        userDao
                                .getLoginUserByEmail(email)
                                .blockOptional()
                                .<Either<String, LoginUser>>map(Either::right)
                                .orElseGet(() -> Either.left("No user found"))
                );
    }
}
