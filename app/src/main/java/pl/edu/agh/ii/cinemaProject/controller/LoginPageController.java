package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.event.LoginEvent;
import pl.edu.agh.ii.cinemaProject.event.StartCyclicCashierNotifyJob;
import pl.edu.agh.ii.cinemaProject.service.LoginService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.net.URL;

@Controller
public class LoginPageController {
    @FXML
    public TextField email;
    @FXML
    public Label errorLabel;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ApplicationContext applicationContext;

    public static URL getFXML() {
        return LoginPageController.class.getResource("/fxml/LoginPage.fxml");
    }

    public void login(ActionEvent actionEvent) {
        loginService.login(email.getText()).fold(
                error -> {
                    errorLabel.setText(error);
                    errorLabel.setVisible(true);
                    return null;
                },
                user -> {
                    SceneChanger.changeScene(MainPageController.getFXML());
                    applicationContext.publishEvent(new LoginEvent(user));
                    applicationContext.publishEvent(new StartCyclicCashierNotifyJob());
                    return null;
                }
        );
    }
}
