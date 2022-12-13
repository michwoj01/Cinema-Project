package pl.edu.agh.ii.cinemaProject.service;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import reactor.core.publisher.Flux;

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

    private Either<String, ?> checkEmptyName(LoginUser user) {
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty()) {
            return Either.left("First name and last name cannot be empty");
        } else {
            return Either.right(Option.none());
        }
    }

    private Either<String, ?> checkEmptyRoleId(LoginUser user) {
        System.out.println(user);
        if (user.getRoleId() <= 0) {
            return Either.left("Role id has to be over 0");
        } else {
            return Either.right(Option.none());
        }
    }

    private Either<String, ?> validateUser(LoginUser user) {
        return checkEmail(user.getEmail()).flatMap(__ -> checkEmptyName(user).flatMap(___ -> checkEmptyRoleId(user)));
    }

    public Either<String, LoginUser> createOrUpdateUser(LoginUser user) {
        return validateUser(user).flatMap((unused) -> Try.ofCallable(() -> userDao.save(user).block()).fold((error) -> {
            if (error instanceof org.springframework.dao.DataIntegrityViolationException) {
                return Either.left("User with this email already exists");
            } else {
                return Either.left("Unknown error");
            }
        }, Either::right));
    }

    public Either<String, LoginUser> login(String email) {
        return checkEmail(email).flatMap((unused) -> userDao.getLoginUserByEmail(email).blockOptional().<Either<String, LoginUser>>map(Either::right).orElseGet(() -> Either.left("No user found")));
    }

    public Flux<LoginUser> getAll() {
        return userDao.findAll();
    }

    public Either<String, Void> deleteUser(long userId) {
        return Try.ofCallable(() -> userDao.deleteById(userId).block()).fold((error) -> Either.left("Unknown error"), (unused) -> Either.right(null));
    }
}
