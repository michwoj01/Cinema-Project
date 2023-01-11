package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;

public class StartCyclicCashierNotifiJob extends ApplicationEvent {
    public StartCyclicCashierNotifiJob() {
        super(new Object());
    }
}
