package pl.edu.agh.ii.cinemaProject.controller;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.service.EmailServiceImpl;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.ReportsService;
import reactor.core.publisher.Flux;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Controller
public class ReportsController {
    @FXML
    ListView<String> mostPopularMovies;
    @FXML
    ListView<String> mostDisplayedMovies;
    @FXML
    BarChart<String, Integer> ticketsSoldPerDay;
    @FXML
    BarChart<String, Integer> moviesDisplayedPerDay;
    @FXML
    Button sendButton;
    @FXML
    VBox mainBox;

    @Autowired
    private MovieService movieService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private ReportsService reportsService;

    private final XYChart.Series<String, Integer> tickets = new XYChart.Series<>();
    private final XYChart.Series<String, Integer> movies = new XYChart.Series<>();

    public static URL getFXML() {
        return ReportsController.class.getResource("/fxml/SendReports.fxml");
    }

    @FXML
    public void initialize() {
        Flux.zip(reportsService.getMoviesDisplayedByDays(), reportsService.getDaysForMovies(),
                (movie, day) -> movies.getData().add(new XYChart.Data<>(day, movie))
        ).doOnComplete(() -> moviesDisplayedPerDay.getData().add(movies)).subscribe();
        Flux.zip(reportsService.getTicketsSoldByDays(), reportsService.getDaysForTickets(),
                (ticket, day) -> tickets.getData().add(new XYChart.Data<>(day, ticket))
        ).doOnComplete(() -> ticketsSoldPerDay.getData().add(tickets)).subscribe();

        this.mostPopularMovies.setItems(FXCollections.observableArrayList(movieService.getMostPopularMovies().collectList().block()));
        this.mostDisplayedMovies.setItems(FXCollections.observableArrayList(movieService.getMostDisplayedMovies().collectList().block()));
    }

    public void sendReportToManagers(ActionEvent actionEvent) {
        WritableImage image = mainBox.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage imageRGB = new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
        Graphics2D graphics = imageRGB.createGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        File file;
        try {
            file = File.createTempFile("snap", ".jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            ImageIO.write(imageRGB, "jpg", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emailService.sendReportsToManagers(reportsService.getManagers(), file);
        file.deleteOnExit();
    }
}
