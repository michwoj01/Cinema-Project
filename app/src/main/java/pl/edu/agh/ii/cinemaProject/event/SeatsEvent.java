package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;

public class SeatsEvent extends ApplicationEvent {
    public SeatsEvent(Schedule schedule) {
        super(schedule);
    }
}
