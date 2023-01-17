package pl.edu.agh.ii.cinemaProject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.edu.agh.ii.cinemaProject.controller.LoginPageController;
import pl.edu.agh.ii.cinemaProject.util.SceneChanger;

public class CinemaJavaFXApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(CinemaProjectApplication.class).run();
        SceneChanger.setApplicationContext(applicationContext);
    }

    @Override
    public void start(Stage stage) {
        SceneChanger.setMainStage(stage);
        SceneChanger.changeScene(LoginPageController.getFXML());
        stage.show();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
