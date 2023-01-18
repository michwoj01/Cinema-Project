package pl.edu.agh.ii.cinemaProject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.enums.PageEnum;
import pl.edu.agh.ii.cinemaProject.event.LoginEvent;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Permission;
import pl.edu.agh.ii.cinemaProject.service.PermissionService;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

import java.net.URL;

@Controller
public class MainPageController implements ApplicationListener<LoginEvent> {
    @FXML
    public ListView<String> categoriesListView;
    @FXML
    public BorderPane mainPane;

    @Autowired
    private PermissionService permissionService;

    public static URL getFXML() {
        return MainPageController.class.getResource("/fxml/MainPage.fxml");
    }

    @FXML
    public void initialize() {
        SceneChanger.setMainPane(mainPane);
        categoriesListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                PageEnum currentPage = PageEnum.valueOf(observable.getValue().toUpperCase());
                SceneChanger.setPane(currentPage.getUrl());
            }
        }));
    }

    @Override
    public void onApplicationEvent(LoginEvent event) {
        permissionService.getPermissionsForUser((LoginUser) event.getSource())
                .filter(Permission::isShouldBeDisplayed)
                .subscribe(permission -> categoriesListView.getItems().add(permission.getName()));
    }

    public void logout(ActionEvent actionEvent) {
        SceneChanger.changeScene(LoginPageController.getFXML());
    }
}
