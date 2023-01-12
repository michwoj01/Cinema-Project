package pl.edu.agh.ii.cinemaProject.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import pl.edu.agh.ii.cinemaProject.service.MovieService;

import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public TextField maxDuration;
    @FXML
    public TextField name;

    @FXML
    public Pagination pagination;
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
                        movieService.deleteMovie(currentItemSelected.getId()).subscribe((movie) -> refreshList());
                    }
                    return bt;
                });
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/css/Alert.css").toExternalForm());
                dialogPane.getStyleClass().add("dialogPane");
                alert.showAndWait();
            }
        });

        this.moviesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                if (empty || movie == null) {
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
        this.minDuration.setOnAction((value) -> refreshList());
        this.maxDuration.setOnAction((value) -> refreshList());

        pagination.setMaxPageIndicatorCount(10);

        pagination.setPageFactory((pageIndex) -> {
            refreshList();
            return new VBox();
        });

        refreshList();
    }

    private void refreshList() {
        Optional<String> newNameFilter = Optional.ofNullable(name.getText()).filter(s -> !s.isEmpty());
        Optional<Integer> newMinDuration = textFieldToOptInt(this.minDuration);
        Optional<Integer> newMaxDuration = textFieldToOptInt(this.maxDuration);
        Platform.runLater(() -> {
            var numberPages = Math.floorDiv(
                    movieService.getMovieCountWithFilter(new MovieFiltersDTO(newMinDuration, newMaxDuration, newNameFilter)).block(),
                    pagination.getMaxPageIndicatorCount()
            );
            pagination.setPageCount(numberPages > 0 ? numberPages : 1);
            this.moviesListView.setItems(FXCollections.observableArrayList(
                    movieService.getMoviesWithFilterDTO(
                                    new MovieFiltersDTO(newMinDuration, newMaxDuration, newNameFilter),
                                    pagination.getCurrentPageIndex(),
                                    pagination.getMaxPageIndicatorCount())
                            .toStream().collect(Collectors.toList())));
        });
    }

    private Optional<Integer> textFieldToOptInt(TextField textField) {
        return Try(() -> Integer.parseInt(textField.getText()))
                .toEither()
                .fold((th) -> Optional.empty(), Optional::of);
    }
}
