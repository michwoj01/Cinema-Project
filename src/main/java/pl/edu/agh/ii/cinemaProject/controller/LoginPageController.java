package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.net.URL;

public class LoginPageController {
    @FXML
    public TextField email;

    public static URL getFXML() {
        return LoginPageController.class.getResource("/fxml/LoginPage.fxml");
    }

    public void login(ActionEvent actionEvent) {
        System.out.println("Login button clicked");
        System.out.println("Email: " + email.getText());
    }
}
