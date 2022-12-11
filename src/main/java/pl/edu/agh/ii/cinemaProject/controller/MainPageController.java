package pl.edu.agh.ii.cinemaProject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.event.LoginEvent;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.service.PermissionService;

import java.net.URL;

@Controller
public class MainPageController implements ApplicationListener<LoginEvent> {

    @FXML
    public ListView<String> categoriesListView;
    @FXML
    public VBox contentPane;
    @Autowired
    private PermissionService permissionService;

    public static URL getFXML() {
        return MainPageController.class.getResource("/fxml/MainPage.fxml");
    }

    @FXML
    public void initialize() {
    }

    @Override
    public void onApplicationEvent(LoginEvent event) {
        permissionService.getPermissionsForUser((LoginUser) event.getSource()).subscribe(permission -> {
            categoriesListView.getItems().add(permission.getName());
            //Change to Permission object (with controller in each (view))
        });
    }
}
