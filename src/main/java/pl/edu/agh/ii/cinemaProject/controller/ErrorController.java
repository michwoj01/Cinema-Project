package pl.edu.agh.ii.cinemaProject.controller;

import org.springframework.stereotype.Controller;

import java.net.URL;

@Controller
public class ErrorController {

    public static URL getFXML() {
        return ErrorController.class.getResource("/fxml/ScheduleMoviePage.fxml");
    }

//    TODO

}
