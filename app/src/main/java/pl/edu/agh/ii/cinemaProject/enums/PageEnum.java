package pl.edu.agh.ii.cinemaProject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.agh.ii.cinemaProject.controller.*;

import java.net.URL;

@RequiredArgsConstructor
@Getter
public enum PageEnum {
    MODIFY_USERS("modify_users", ModifyUserController.getFXML()),
    SCHEDULE_MOVIES("schedule_movies", ScheduleMoviesController.getFXML()),
    MODIFY_SCHEDULE_WATCHERS("modify_schedule_watchers", ModifyScheduleWatchersController.getFXML()),
    MOVIES("movies", MovieController.getFXML()),
    EDIT_NOTIFICATION_MESSAGE("edit_notification_message", NotificationController.getFXML()),
    ERROR("error", ErrorController.getFXML()),
    SEND_REPORTS("send_reports", SendReportsController.getFXML());
    private final String name;
    private final URL url;
}