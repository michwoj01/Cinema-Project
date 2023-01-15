package pl.edu.agh.ii.cinemaProject.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.model.Notification;
import pl.edu.agh.ii.cinemaProject.service.NotificationService;

import java.net.URL;
import java.util.Optional;

@Controller
public class NotificationController {
    @FXML
    private TextArea messageTextArea;
    @Autowired
    private NotificationService notificationService;
    private Optional<Notification> maybeNotification = Optional.empty();

    public static URL getFXML() {
        return NotificationController.class.getResource("/fxml/NotificationTextEdit.fxml");
    }

    @FXML
    public void initialize() {
        notificationService.getCashierNotification().subscribe(notification -> {
            Platform.runLater(() -> messageTextArea.setText(notification.getMessage()));
            maybeNotification = Optional.of(notification);
        });
    }

    public void saveMessage(ActionEvent actionEvent) {
        maybeNotification.ifPresentOrElse(notification -> {
            notification.setMessage(messageTextArea.getText());
            notificationService.saveNotification(notification).subscribe();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Message saved");
            alert.showAndWait();
        }, () -> {
            System.err.println("Notification not found");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Notification not found");
            alert.showAndWait();
        });
    }

    public void restoreMessage(ActionEvent actionEvent) {
        maybeNotification.ifPresentOrElse(notification -> {
            messageTextArea.setText(notification.getMessage());
        }, () -> {
            System.err.println("Notification not found");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Notification not found");
            alert.showAndWait();
        });
    }
}
