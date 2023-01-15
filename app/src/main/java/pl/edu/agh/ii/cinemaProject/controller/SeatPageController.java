package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.event.SeatsEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import pl.edu.agh.ii.cinemaProject.model.Seat;
import pl.edu.agh.ii.cinemaProject.service.CinemaHallService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;
import pl.edu.agh.ii.cinemaProject.service.SeatService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Controller
public class SeatPageController implements ApplicationListener<SeatsEvent> {
    @FXML
    public VBox seatBox;
    @FXML
    public Button buyButton;

    @Autowired
    public SeatService seatService;
    @Autowired
    public ScheduleService scheduleService;
    @Autowired
    public CinemaHallService cinemaHallService;

    private HashMap<Integer, Button> seatMap;
    private Set<Seat> selectedSeats;
    private Schedule selectedSchedule;

    public static URL getFXML() {
        return SeatPageController.class.getResource("/fxml/SeatPage.fxml");
    }

    @FXML
    @Override
    public void onApplicationEvent(SeatsEvent event) {
        selectedSchedule = (Schedule) event.getSource();
        selectedSeats = new HashSet<>();
        seatMap = new HashMap<>();

        var size = cinemaHallService.getCinemaHallById(selectedSchedule.getCinema_hall_id()).block().getSize();
        var seatsPerRow = Math.floor(Math.sqrt(size));

        HBox currentRow = new HBox();
        seatBox.getChildren().removeAll();

        for (int number = 0; number < size; number++) {
            if (number % seatsPerRow == 0) {
                currentRow = new HBox();
                seatBox.getChildren().add(currentRow);
            }

            var button = new Button(String.valueOf(number + 1));
            button.setMinSize(400 / seatsPerRow, 400 / seatsPerRow);
            button.setStyle("-fx-font-size: " + 150 / seatsPerRow + ";");

            button.setOnAction(selectEvent -> {
                if (button.getStyleClass().size() == 1) {
                    if (selectedSeats.isEmpty()) buyButton.setDisable(false);
                    button.getStyleClass().add("clickedButton");
                    var ticket = new Seat();
                    ticket.setSchedule_id(selectedSchedule.getId());
                    ticket.setSeat_no(Integer.parseInt(button.getText()));
                    selectedSeats.add(ticket);
                } else {
                    button.getStyleClass().remove(1);
                    selectedSeats.removeIf(seat -> seat.getSeat_no() == Integer.parseInt(button.getText()));
                    if (selectedSeats.isEmpty()) buyButton.setDisable(true);
                }
            });

            seatMap.put(number + 1, button);
            currentRow.getChildren().add(button);
        }
        seatService.getTakenSeats(selectedSchedule.getId()).subscribe(seat -> {
            var temp = seat.getSeat_no();
            seatMap.get(temp).setDisable(true);
        });
    }

    public void handleBackAction(ActionEvent actionEvent) {
        SceneChanger.setPane(ModifyScheduleWatchersController.getFXML());
    }

    public void handleBuyAction(ActionEvent actionEvent) {
        selectedSeats.forEach(seat -> {
            seatService.takeSeat(seat).subscribe();
            seatMap.get(seat.getSeat_no()).setDisable(true);
        });
        scheduleService.buyTickets(selectedSchedule.getId(), selectedSeats.size()).subscribe();
        selectedSeats.clear();
    }
}
