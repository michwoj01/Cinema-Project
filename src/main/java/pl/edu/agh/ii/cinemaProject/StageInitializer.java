package pl.edu.agh.ii.cinemaProject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.edu.agh.ii.cinemaProject.controller.LoginPageController;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<CinemaJavaFXApplication.StageReadyEvent> {
    @Override
    public void onApplicationEvent(CinemaJavaFXApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(LoginPageController.getFXML()), 800, 600));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("hello, first page");
        stage.show();
    }
}
