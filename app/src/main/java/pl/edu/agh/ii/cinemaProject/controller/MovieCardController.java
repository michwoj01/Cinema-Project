package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.event.MovieCardEvent;
import pl.edu.agh.ii.cinemaProject.event.SeatsEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;
import pl.edu.agh.ii.cinemaProject.service.TicketService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.net.URL;
import java.time.format.DateTimeFormatter;

@Controller
public class MovieCardController implements ApplicationListener<MovieCardEvent> {
    @FXML
    public ImageView moviePoster;
    @FXML
    public Label titleLabel;
    @FXML
    public Label descriptionLabel;
    @FXML
    public Label availableSeatsLabel;
    @FXML
    public Label cinemaHallLabel;
    @FXML
    public Label dateLabel;
    @FXML
    public Label durationLabel;
    @FXML
    public Button buyButton;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ApplicationContext applicationContext;

    private Schedule selectedSchedule;

    public static URL getFXML() {
        return ModifyScheduleWatchersController.class.getResource("/fxml/MovieCardPage.fxml");
    }

    @FXML
    @Override
    public void onApplicationEvent(MovieCardEvent event) {
        selectedSchedule = (Schedule) event.getSource();
        reloadLabels();
    }

    private void reloadLabels() {
        var movie = movieService.getMovieInfo(selectedSchedule.getMovie_id()).block();
        titleLabel.setText(movie.getName());
        var image = new Image(movie.getCover_url(), 300, 0, true, true);
        moviePoster.setImage(image);
        descriptionLabel.setText(movie.getDescription());
        availableSeatsLabel.setText(String.valueOf(scheduleService.countAvailableSeats(selectedSchedule.getId()).block()));
        cinemaHallLabel.setText(String.valueOf(selectedSchedule.getCinema_hall_id()));
        dateLabel.setText(DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm a").format(selectedSchedule.getStart_date()));
        durationLabel.setText(String.valueOf(movie.getDuration()));
    }

    public void handleBuyTicketAction(ActionEvent actionEvent) {
        SceneChanger.setPane(SeatPageController.getFXML());
        applicationContext.publishEvent(new SeatsEvent(selectedSchedule));
    }

    public void handleBackAction(ActionEvent actionEvent) {
        SceneChanger.setPane(ModifyScheduleWatchersController.getFXML());
    }
}
