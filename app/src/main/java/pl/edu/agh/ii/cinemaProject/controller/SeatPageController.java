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
import pl.edu.agh.ii.cinemaProject.model.Ticket;
import pl.edu.agh.ii.cinemaProject.service.CinemaHallService;
import pl.edu.agh.ii.cinemaProject.service.ScheduleService;
import pl.edu.agh.ii.cinemaProject.service.TicketService;
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

    private HashMap<Integer,Button> seatMap;

    private Set<Ticket> selectedTickets;

    private Schedule selectedSchedule;

    @Autowired
    public TicketService ticketService;

    @Autowired
    public ScheduleService scheduleService;

    @Autowired
    public CinemaHallService cinemaHallService;

    public static URL getFXML() {
        return SeatPageController.class.getResource("/fxml/SeatPage.fxml");
    }

    @FXML
    @Override
    public void onApplicationEvent(SeatsEvent event) {
        selectedSchedule = (Schedule) event.getSource();
        selectedTickets =new HashSet<>();
        seatMap= new HashMap<>();

        var size=cinemaHallService.getCinemaHallById(selectedSchedule.getCinema_hall_id()).block().getSize();
        var seatsPerRow=Math.floor(Math.sqrt(size));

        HBox currentRow = new HBox();
        seatBox.getChildren().removeAll();

        for(int number=0; number<size; number++){
            if(number%seatsPerRow==0){
                currentRow=new HBox();
                seatBox.getChildren().add(currentRow);
            }

            var button=new Button(String.valueOf(number+1));
            button.setMinSize(400/seatsPerRow,400/seatsPerRow);
            button.setStyle("-fx-font-size: "+150/seatsPerRow+";");

            button.setOnAction(selectEvent -> {
                if (button.getStyleClass().size()==1) {
                    if(selectedTickets.isEmpty())buyButton.setDisable(false);
                    button.getStyleClass().add("clickedButton");
                    var ticket=new Ticket();
                    ticket.setSchedule_id(selectedSchedule.getId());
                    ticket.setSeat_nr(Integer.parseInt(button.getText()));
                    selectedTickets.add(ticket);
                }
                else{
                    button.getStyleClass().remove(1);
                    selectedTickets.removeIf(ticket -> ticket.getSeat_nr()==Integer.parseInt(button.getText()));
                    if(selectedTickets.isEmpty())buyButton.setDisable(true);
                }
            });

            seatMap.put(number+1,button);
            currentRow.getChildren().add(button);
        }
        ticketService.getTakenSeats(selectedSchedule.getId()).subscribe(ticket -> {
            var temp= ticket.getSeat_nr();
            seatMap.get(temp).setDisable(true);
        });
    }

    public void handleBackAction(ActionEvent actionEvent) {
        SceneChanger.setPane(ModifyScheduleWatchersController.getFXML());
    }

    public void handleBuyAction(ActionEvent actionEvent) {
        selectedTickets.forEach(ticket -> {
            ticketService.takeSeat(ticket).subscribe();
            seatMap.get(ticket.getSeat_nr()).setDisable(true);
        });
        scheduleService.buyTickets(selectedSchedule.getId(), selectedTickets.size()).subscribe();
        selectedTickets.clear();
    }
}
