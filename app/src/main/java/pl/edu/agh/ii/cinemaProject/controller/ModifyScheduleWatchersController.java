package pl.edu.agh.ii.cinemaProject.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;

import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Controller
public class ModifyScheduleWatchersController {

    @FXML
    public ListView<Schedule> scheduledMoviesListView;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private MovieService movieService;

    public static URL getFXML() {
        return ModifyScheduleWatchersController.class.getResource("/fxml/ModifyScheduleWatchersPage.fxml");
    }
    @FXML
    public void initialize() {
        scheduledMoviesListView.setFixedCellSize(200);
        scheduledMoviesListView.setCellFactory(param -> new ListCell<Schedule>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Schedule newSchedule, boolean empty) {
                super.updateItem(newSchedule, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    var aa = movieService.getMovieInfo(newSchedule.getMovie_id()).block();
                    var image = new Image(aa.getCover_url(), 100, 0, true, true);
                    imageView.setImage(image);

                    setGraphic(imageView);
                    setPrefHeight(200);
                }
            }
        });

        scheduledMoviesListView.setItems(FXCollections.observableList(
                List.of(
                        new Schedule(1, Date.from(Instant.ofEpochSecond(1)), 10,1,1),
                        new Schedule(2, Date.from(Instant.ofEpochSecond(1)), 10,600,1))));
    }

}
