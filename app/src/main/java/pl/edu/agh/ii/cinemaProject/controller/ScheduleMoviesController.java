package pl.edu.agh.ii.cinemaProject.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LongStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.model.CinemaHall;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.service.CinemaHallService;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

@Controller
public class ScheduleMoviesController {
    @FXML
    private TableView<Schedule> scheduleView;
    @FXML
    private TableColumn<Schedule, Movie> scheduleMovie;
    @FXML
    private TableColumn<Schedule, CinemaHall> scheduleHall;
    @FXML
    private TableColumn<Schedule, LocalDateTime> scheduleDate;
    @FXML
    private Button deleteButton;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private CinemaHallService cinemaHallService;

    private final LocalDateTimeStringConverter localDateTimeStringConverter = new LocalDateTimeStringConverter();
    private final LongStringConverter longStringConverter = new LongStringConverter();
    private final StringConverter<Movie> movieStringConverter = new StringConverter<>() {
        @Override
        public String toString(Movie object) {
            return object.getName();
        }

        @Override
        public Movie fromString(String string) {
            return movieService.getMovieByName(string).block();
        }
    };
    private final StringConverter<CinemaHall> cinemaHallStringConverter = new StringConverter<>() {
        @Override
        public String toString(CinemaHall object) {
            return object.getName();
        }

        @Override
        public CinemaHall fromString(String string) {
            return cinemaHallService.findAll().filter((cinemaHall) -> cinemaHall.getName().equals(string)).blockFirst();
        }
    };


    public static URL getFXML() {
        return ScheduleMoviesController.class.getResource("/fxml/ScheduleMoviePage.fxml");
    }

    @FXML
    private void initialize() {
        scheduleView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        scheduleMovie.setCellValueFactory(schedule -> new SimpleObjectProperty<>(movieService.getMovieInfo(schedule.getValue().getMovie_id()).block()));
        scheduleMovie.setCellFactory(ComboBoxTableCell.forTableColumn(movieStringConverter, FXCollections.observableArrayList(movieService.findAll().collectList().block())));
        scheduleMovie.setOnEditCommit(e -> performUpdate(e, (schedule, movie) -> schedule.setMovie_id(movie.getId())));

        scheduleHall.setCellValueFactory(schedule -> new SimpleObjectProperty<>(cinemaHallService.getCinemaHallById(schedule.getValue().getCinema_hall_id()).block()));
        scheduleHall.setCellFactory(ComboBoxTableCell.forTableColumn(cinemaHallStringConverter, FXCollections.observableArrayList(cinemaHallService.findAll().collectList().block())));
        scheduleHall.setOnEditCommit(e -> performUpdate(e, (schedule, hall) -> {
            schedule.setCinema_hall_id(hall.getId());
            schedule.setCurrently_available(hall.getSize());
            schedule.setNr_of_seats(hall.getSize());
        }));

        scheduleDate.setCellValueFactory(new PropertyValueFactory<>("start_date"));
        scheduleDate.setCellFactory(TextFieldTableCell.forTableColumn(localDateTimeStringConverter));
        scheduleDate.setOnEditCommit(e -> performUpdate(e, Schedule::setStart_date));

        deleteButton.disableProperty().bind(Bindings.isEmpty(scheduleView.getSelectionModel().getSelectedItems()));
        scheduleService.findAll().toStream().forEach(user -> scheduleView.getItems().add(user));
    }

    public void handleAddUserAction(ActionEvent actionEvent) {
        var hall= cinemaHallService.getCinemaHallById(1).block();
        var newSchedule = new Schedule();
        newSchedule.setMovie_id(1);
        newSchedule.setStart_date(LocalDateTime.of(2023, 1, 31, 12, 0, 0));
        newSchedule.setCinema_hall_id(1);
        newSchedule.setCurrently_available(hall != null ? hall.getSize() : 0);
        newSchedule.setNr_of_seats(hall != null ? hall.getSize() : 0);
        scheduleView.getItems().add(newSchedule);
    }

    public void handleDeleteScheduleAction(ActionEvent actionEvent) {
        scheduleView.getSelectionModel().getSelectedItems().forEach(schedule -> {
            if (schedule.getId() != 0) {
                scheduleService.deleteSchedule(schedule).fold((error) -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Error while deleting schedule");
                    alert.setContentText("Error: " + error);
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/css/Alert.css").toExternalForm());
                    dialogPane.getStyleClass().add("dialogPane");

                    alert.showAndWait();
                    return null;
                }, (__) -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("OK");
                    alert.setHeaderText("Successfully deleted schedule");
                    alert.setContentText("Schedule: " + schedule);
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/css/Alert.css").toExternalForm());
                    dialogPane.getStyleClass().add("dialogPane");

                    alert.showAndWait();
                    return null;
                });
            }
            scheduleView.getItems().remove(schedule);
        });

    }

    private <T> void performUpdate(TableColumn.CellEditEvent<Schedule, T> cellEditEvent, BiConsumer<Schedule, T> updateFunction) {
        var pair = getOldAndNewValue(cellEditEvent);
        updateFunction.accept(pair.getFirst(), pair.getSecond());

        scheduleService.createOrUpdateSchedule(pair.getFirst()).fold((error) -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Error while updating schedule");
            alert.setContentText("Error: " + error);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/Alert.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.showAndWait();
            return null;
        }, (schedule) -> {
            var viewModel = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
            viewModel.setId(schedule.getId());
            updateFunction.accept(viewModel, cellEditEvent.getNewValue());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OK");
            alert.setHeaderText("Successfully saved schedule");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/Alert.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.showAndWait();
            return null;
        });

    }

    private <T> Pair<Schedule, T> getOldAndNewValue(TableColumn.CellEditEvent<Schedule, T> event) {
        var oldValue = event.getTableView().getItems().get(event.getTablePosition().getRow());
        var changedValue = event.getNewValue();
        return Pair.of(oldValue, changedValue);
    }
}
