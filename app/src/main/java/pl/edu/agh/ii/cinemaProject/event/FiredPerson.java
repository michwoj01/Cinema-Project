package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;

public class FiredPerson extends ApplicationEvent {
    public FiredPerson(LoginUser user) {
        super(user);
    }
}
