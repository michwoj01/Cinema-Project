package pl.edu.agh.ii.cinemaProject.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import pl.edu.agh.ii.cinemaProject.service.MovieService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.Try;


@Controller
public class MovieController {
    private final String DELETE_BUTTON_TEXT = "Delete";
    private final String HIDE_BUTTON_TEXT = "Hide";
    @FXML
    public ListView<Movie> moviesListView;
    @FXML
    public TextField minDuration;
    @FXML
    public TextField name;
    @Autowired
    private MovieService movieService;

    public static URL getFXML() {
        return MovieController.class.getResource("/fxml/EditMovies.fxml");
    }

    @FXML
    void initialize() {
        this.moviesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        this.moviesListView.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                var currentItemSelected = this.moviesListView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.NONE);

                alert.getButtonTypes().add(new ButtonType(HIDE_BUTTON_TEXT));
                alert.getButtonTypes().add(new ButtonType(DELETE_BUTTON_TEXT));

                alert.setTitle(currentItemSelected.getName());
                alert.setHeaderText("Description: " + currentItemSelected.getDescription());
                alert.setContentText("duration: " + currentItemSelected.getDuration());


                alert.setResultConverter((bt) -> {
                    if (bt.getText().equals(DELETE_BUTTON_TEXT)) {
                        movieService.deleteMovie(currentItemSelected.getId()).mapNotNull(x -> {
                            refreshList();
                            return null;
                        }).block();
                    }
                    return bt;
                });
                alert.showAndWait();
            }
        });

        this.moviesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                if (empty || movie==null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(movie.getName());
                    Platform.runLater(() -> {
                        ImageView imageView = new ImageView();
                        var image = new Image(movie.getCover_url(), 100, 0, true, true);
                        imageView.setImage(image);
                        setGraphic(imageView);
                        setPrefHeight(200);
                    });
                }
            }
        });

        this.name.setOnAction((value) -> refreshList());


        this.moviesListView.setItems(FXCollections.observableList(
                Stream.of(200, 201, 601, 602, 603, 604, 605, 606, 608).map((x) -> movieService.getMovieInfo(x).block()).collect(Collectors.toList())));

    }

    private void refreshList() {
        var newNameFilter = this.name.getText();
        Optional<Integer> newMinDuration = textFieldToOptInt(this.minDuration);
        if (!Objects.equals(newNameFilter, "")) {
            Platform.runLater(() -> this.moviesListView.setItems(FXCollections.observableList(new ArrayList<>())));
            movieService
                    .getMoviesWithFilterDTO(new MovieFiltersDTO(newMinDuration, Optional.empty(), Optional.of(newNameFilter)))
                    .map((movie) -> {
                        Platform.runLater(() -> this.moviesListView.getItems().add(movie));
                        return movie;
                    }).collectList().block();
        }
    }

    private Optional<Integer> textFieldToOptInt(TextField textField) {
        return Try(() -> Integer.parseInt(this.minDuration.getText()))
                .toEither()
                .fold((th) -> Optional.empty(), Optional::of);
    }

}
