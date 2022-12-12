package pl.edu.agh.ii.cinemaProject.enums;

import pl.edu.agh.ii.cinemaProject.controller.ErrorController;
import pl.edu.agh.ii.cinemaProject.controller.ModifyScheduleWatchersController;
import pl.edu.agh.ii.cinemaProject.controller.ModifyUserController;
import pl.edu.agh.ii.cinemaProject.controller.ScheduleMoviesController;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PageEnum {
    MODIFY_USERS("modify_users", ModifyUserController.getFXML()),
    SCHEDULE_MOVIES("schedule_movies", ScheduleMoviesController.getFXML()),
    MODIFY_SCHEDULE_WATCHERS("modify_schedule_watchers", ModifyScheduleWatchersController.getFXML()),
    ERROR("error", ErrorController.getFXML());
    private final String name;
    private final URL url;
    private static final Map<String, PageEnum> ENUM_MAP;

    PageEnum(String name, URL fxml) {
        this.name = name;
        this.url=fxml;
    }

    public String getName() {
        return this.name;
    }

    public URL getUrl(){
        return this.url;
    }

    static {
        Map<String, PageEnum> map = new ConcurrentHashMap<>();
        for (PageEnum instance : PageEnum.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static PageEnum get(String name) {
        return ENUM_MAP.getOrDefault(name,ERROR);
    }
}