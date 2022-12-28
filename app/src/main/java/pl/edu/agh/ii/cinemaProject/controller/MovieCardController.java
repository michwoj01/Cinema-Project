package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.event.ScheduleEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.net.URL;

@Controller
public class MovieCardController implements ApplicationListener<ScheduleEvent>{

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
    public TextField numberOfSeats;

    @Autowired
    private MovieService movieService;

    private Schedule selectedSchedule;

    @FXML
    public Button buyButton;

    @Autowired
    private ScheduleService scheduleService;

    public static URL getFXML() {
        return ModifyScheduleWatchersController.class.getResource("/fxml/MovieCardPage.fxml");
    }

    @FXML
    public void initialize() {
        numberOfSeats.textProperty().addListener((observable, oldValue, newValue) -> {
            buyButton.setDisable(newValue.matches(""));
            if(!newValue.matches("")){
                if (!newValue.matches("\\d*") || newValue.matches("0")) {
                    numberOfSeats.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(Integer.parseInt(newValue)>selectedSchedule.getCurrently_available()){
                    numberOfSeats.setText(String.valueOf(selectedSchedule.getCurrently_available()));
                }
            }
        });
    }
    @FXML
    @Override
    public void onApplicationEvent(ScheduleEvent event) {
        selectedSchedule=(Schedule) event.getSource();
        reloadLabels();
    }

    private void reloadLabels(){
        var movie = movieService.getMovieInfo(selectedSchedule.getMovie_id()).block();
        titleLabel.setText(movie.getName());
        var image = new Image(movie.getCover_url(), 300, 0, true, true);
        moviePoster.setImage(image);
        descriptionLabel.setText(movie.getDescription());
        availableSeatsLabel.setText(String.valueOf(selectedSchedule.getCurrently_available()));
        cinemaHallLabel.setText(String.valueOf(selectedSchedule.getCinema_hall_id()));
        dateLabel.setText(String.valueOf(selectedSchedule.getStart_date()));
        durationLabel.setText(String.valueOf(movie.getDuration()));
    }

    public void handleBuyTicketAction(ActionEvent actionEvent) {
        var number=Integer.parseInt(numberOfSeats.getText());
        scheduleService.buyTickets(selectedSchedule.getId(),number).subscribe();
        selectedSchedule.setCurrently_available(selectedSchedule.getCurrently_available()-number);
        availableSeatsLabel.setText(String.valueOf(selectedSchedule.getCurrently_available()));
        numberOfSeats.setText("");
    }

    public void handleBackAction(ActionEvent actionEvent){
        SceneChanger.setPane(ModifyScheduleWatchersController.getFXML());
    }
}
