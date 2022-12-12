package pl.edu.agh.ii.cinemaProject.controller;

import org.springframework.stereotype.Controller;

import java.net.URL;

@Controller
public class ModifyScheduleWatchersController {

    public static URL getFXML() {
        return ModifyScheduleWatchersController.class.getResource("/fxml/ModifyScheduleWatchersPage.fxml");
    }
}
