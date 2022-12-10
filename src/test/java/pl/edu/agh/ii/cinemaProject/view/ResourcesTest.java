package pl.edu.agh.ii.cinemaProject.view;

import org.junit.jupiter.api.Test;
import pl.edu.agh.ii.cinemaProject.controller.LoginPageController;
import pl.edu.agh.ii.cinemaProject.controller.MainPageController;

import java.io.IOException;

// Javafx platform is not initialized, so openStream on Uri should work (don't use relative paths)
public class ResourcesTest {

    @Test
    public void testLoginPageResource() throws IOException {
        assert LoginPageController.getFXML().openStream().read() != 0;
    }

    @Test
    public void testMainPageResource() throws IOException {
        assert MainPageController.getFXML().openStream().read() != 0;
    }
}
