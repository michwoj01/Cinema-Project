package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;

public class StartCyclicCashierNotifyJob extends ApplicationEvent {
    public StartCyclicCashierNotifyJob() {
        super(new Object());
    }
}
