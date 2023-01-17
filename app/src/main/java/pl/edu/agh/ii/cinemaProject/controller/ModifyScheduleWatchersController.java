package pl.edu.agh.ii.cinemaProject.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.event.MovieCardEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.service.CinemaHallService;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.net.URL;
import java.time.format.DateTimeFormatter;

@Controller
public class ModifyScheduleWatchersController {
    @FXML
    private TableView<Schedule> scheduleMovieTableView;
    @FXML
    private TableColumn<Schedule, ImageView> movieImage;
    @FXML
    private TableColumn<Schedule, String> movieTitle;
    @FXML
    private TableColumn<Schedule, String> movieDate;
    @FXML
    private TableColumn<Schedule, String> movieCinemaHall;
    @FXML
    private TableColumn<Schedule, Integer> movieAvailableSeats;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private CinemaHallService cinemaHallService;

    public static URL getFXML() {
        return ModifyScheduleWatchersController.class.getResource("/fxml/ModifyScheduleWatchersPage.fxml");
    }

    @FXML
    public void initialize() {
        scheduleMovieTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        movieImage.setCellValueFactory(cellData -> {
            final ImageView imageview = new ImageView();
            movieService.getMovieById(cellData.getValue().getMovie_id()).subscribe((movie) -> Platform.runLater(() -> {
                var image = new Image(movie.getCover_url(), 100, 0, true, true);
                imageview.setImage(image);
            }));
            return new SimpleObjectProperty<>(imageview);
        });
        movieTitle.setCellValueFactory(data -> new SimpleObjectProperty<>(movieService.getMovieById(data.getValue().getMovie_id()).block().getName()));
        movieDate.setCellValueFactory(data -> new SimpleObjectProperty<>(DateTimeFormatter.ofPattern("dd/MM/yyyy \nhh:mm a").format(data.getValue().getStart_date())));
        movieCinemaHall.setCellValueFactory(data -> new SimpleObjectProperty<>(cinemaHallService.getCinemaHallById(data.getValue().getCinema_hall_id()).block().getName()));
        movieAvailableSeats.setCellValueFactory(data -> new SimpleObjectProperty<>(scheduleService.countAvailableSeats(data.getValue().getId()).block()));

        scheduleMovieTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SceneChanger.setPane(MovieCardController.getFXML());
                applicationContext.publishEvent(new MovieCardEvent(observable.getValue()));
            }
        }));

        scheduleService.findAllAvailable().toStream().forEach(schedule -> scheduleMovieTableView.getItems().add(schedule));
    }
}
