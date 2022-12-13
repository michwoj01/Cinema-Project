package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.enums.PageEnum;
import pl.edu.agh.ii.cinemaProject.event.LoginEvent;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.service.PermissionService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.io.IOException;
import java.net.URL;

@Controller
public class MainPageController implements ApplicationListener<LoginEvent> {
    @FXML
    public ListView<String> categoriesListView;
    @FXML
    public BorderPane mainPane;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    public static URL getFXML() {
        return MainPageController.class.getResource("/fxml/MainPage.fxml");
    }

    @FXML
    public void initialize() {
        categoriesListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null){
                PageEnum currentPage= PageEnum.get(observable.getValue());
                setPane(currentPage.getUrl());
            }
        }));
    }

    @Override
    public void onApplicationEvent(LoginEvent event) {
        permissionService.getPermissionsForUser((LoginUser) event.getSource()).subscribe(permission -> {
            categoriesListView.getItems().add(permission.getName());
            //Change to Permission object (with controller in each (view))
        });
    }

    public void setPane(URL fxml) {
        var fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        try {
            mainPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void logout(ActionEvent actionEvent) {
        SceneChanger.changeScene(LoginPageController.getFXML());
    }
}
