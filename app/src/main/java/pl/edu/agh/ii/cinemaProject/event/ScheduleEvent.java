package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;

public class ScheduleEvent extends ApplicationEvent {
    public ScheduleEvent(Schedule schedule) {
        super(schedule);
    }
}
