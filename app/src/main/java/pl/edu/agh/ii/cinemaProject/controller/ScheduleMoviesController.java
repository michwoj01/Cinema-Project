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
    private TableColumn<Schedule, Long> scheduleTickets;
    @FXML
    private Button deleteButton;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private CinemaHallService cinemaHallService;

    public static LocalDateTimeStringConverter localDateTimeStringConverter = new LocalDateTimeStringConverter();
    public static LongStringConverter longStringConverter = new LongStringConverter();

    public static URL getFXML() {
        return ScheduleMoviesController.class.getResource("/fxml/ScheduleMoviePage.fxml");
    }

    @FXML
    private void initialize() {
        scheduleView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        scheduleMovie.setCellValueFactory((schedule) -> new SimpleObjectProperty<>(movieService.getMovieByScheduleId(schedule.getValue().getMovie_id()).block()));
        scheduleMovie.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(movieService.findAll().collectList().block())));
        scheduleMovie.setOnEditCommit(e -> performUpdate(e, (schedule, movie) -> schedule.setMovie_id(movie.getId())));

        scheduleHall.setCellValueFactory(schedule -> new SimpleObjectProperty<>(cinemaHallService.getCinemaHallByScheduleId(schedule.getValue().getCinema_hall_id()).block()));
        scheduleHall.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(cinemaHallService.findAll().collectList().block())));
        scheduleHall.setOnEditCommit(e -> performUpdate(e, (schedule, hall) -> schedule.setCinema_hall_id(hall.getId())));

        scheduleDate.setCellValueFactory(schedule -> new SimpleObjectProperty<>(schedule.getValue().getStart_date()));
        scheduleDate.setCellFactory(column ->
                new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            this.setText(localDateTimeStringConverter.toString(item));
                        }
                    }
                });
        scheduleDate.setOnEditCommit(e -> performUpdate(e, Schedule::setStart_date));

        scheduleTickets.setCellValueFactory(new PropertyValueFactory<>("currently_available"));
        scheduleTickets.setCellFactory(TextFieldTableCell.forTableColumn(longStringConverter));
        scheduleTickets.setOnEditCommit(e -> performUpdate(e, Schedule::setCurrently_available));

        deleteButton.disableProperty().bind(Bindings.isEmpty(scheduleView.getSelectionModel().getSelectedItems()));
        scheduleService.findAll().toStream().forEach(user -> scheduleView.getItems().add(user));
    }

    public void handleAddUserAction(ActionEvent actionEvent) {
        var newSchedule = new Schedule();
        newSchedule.setStart_date(LocalDateTime.now().plusDays(1));
        scheduleView.getItems().add(newSchedule);
    }

    public void handleDeleteUserAction(ActionEvent actionEvent) {
        scheduleView.getSelectionModel().getSelectedItems().forEach(schedule -> {
            if (schedule.getId() != 0) {
                scheduleService.deleteSchedule(schedule.getId()).fold((error) -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Error while deleting schedule");
                    alert.setContentText("Error: " + error);

                    alert.showAndWait();
                    return null;
                }, (__) -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("OK");
                    alert.setHeaderText("Succesfully deleted schedule");
                    alert.setContentText("Schedule: " + schedule);

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
            alert.setHeaderText("Error while updating user");
            alert.setContentText("Error: " + error);

            alert.showAndWait();
            return null;
        }, (schedule) -> {
            var viewModel = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
            viewModel.setId(schedule.getId());
            updateFunction.accept(viewModel, cellEditEvent.getNewValue());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OK");
            alert.setHeaderText("Succesfully saved user");

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
