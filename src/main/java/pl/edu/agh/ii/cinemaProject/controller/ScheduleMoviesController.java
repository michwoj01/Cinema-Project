package pl.edu.agh.ii.cinemaProject.controller;

import org.springframework.stereotype.Controller;

import java.net.URL;

@Controller
public class ScheduleMoviesController {

    public static URL getFXML() {
        return ScheduleMoviesController.class.getResource("/fxml/ScheduleMoviePage.fxml");
    }

//    TODO
}
