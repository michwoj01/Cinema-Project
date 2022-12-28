package pl.edu.agh.ii.cinemaProject.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.LocalDateTimeStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.service.CinemaHallService;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;

import java.net.URL;

@Controller
public class ScheduleMoviesController {
    @FXML
    private TableView<Schedule> scheduleView;
    @FXML
    private TableColumn<Schedule, String> scheduleMovie;
    @FXML
    private TableColumn<Schedule, String> scheduleHall;
    @FXML
    private TableColumn<Schedule, String> scheduleDate;
    @FXML
    private TableColumn<Schedule, Long> scheduleTickets;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private CinemaHallService cinemaHallService;

    public static LocalDateTimeStringConverter localDateTimeStringConverter = new LocalDateTimeStringConverter();

    public static URL getFXML() {
        return ScheduleMoviesController.class.getResource("/fxml/ScheduleMoviePage.fxml");
    }

    @FXML
    private void initialize() {
        scheduleView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        scheduleMovie.setCellValueFactory(schedule -> new SimpleObjectProperty<>(movieService.getMovieInfo(schedule.getValue().getMovie_id()).block().getName()));
        scheduleHall.setCellValueFactory(schedule -> {
            var cinemaHall = cinemaHallService.getCinemaHallByScheduleId(schedule.getValue().getCinema_hall_id()).block();
            return new SimpleStringProperty(String.valueOf(cinemaHall.getName()));
        });
        scheduleDate.setCellValueFactory(schedule -> new SimpleStringProperty(localDateTimeStringConverter.toString(schedule.getValue().getStart_date())));
        scheduleTickets.setCellValueFactory(new PropertyValueFactory<>("currently_available"));
        scheduleService.findAll().toStream().forEach(schedule -> scheduleView.getItems().add(schedule));
    }
}
