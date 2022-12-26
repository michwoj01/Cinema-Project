package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;

public class LoginEvent extends ApplicationEvent {
    public LoginEvent(LoginUser user) {
        super(user);
    }
}
