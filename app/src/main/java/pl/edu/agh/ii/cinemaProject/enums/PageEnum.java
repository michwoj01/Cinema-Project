package pl.edu.agh.ii.cinemaProject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.agh.ii.cinemaProject.controller.*;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Getter
public enum PageEnum {
    MODIFY_USERS("modify_users", ModifyUserController.getFXML()),
    SCHEDULE_MOVIES("schedule_movies", ScheduleMoviesController.getFXML()),
    MODIFY_SCHEDULE_WATCHERS("modify_schedule_watchers", ModifyScheduleWatchersController.getFXML()),
    MOVIES("movies", MovieController.getFXML()),
    ERROR("error", ErrorController.getFXML());
    private static final Map<String, PageEnum> ENUM_MAP;
    private final String name;
    private final URL url;

    static {
        Map<String, PageEnum> map = new ConcurrentHashMap<>();
        for (PageEnum instance : PageEnum.values()) {
            map.put(instance.getName(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static PageEnum get(String name) {
        return ENUM_MAP.getOrDefault(name, ERROR);
    }
}