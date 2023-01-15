package pl.edu.agh.ii.cinemaProject.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.event.LoginEvent;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import pl.edu.agh.ii.cinemaProject.model.Recommendation;
import pl.edu.agh.ii.cinemaProject.service.MovieService;
import pl.edu.agh.ii.cinemaProject.service.PermissionService;
import pl.edu.agh.ii.cinemaProject.service.RecommendationService;
import pl.edu.agh.ii.cinemaProject.service.RoleService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.vavr.API.Try;


@Controller
public class MovieController {
    private final String DELETE_BUTTON_TEXT = "Delete";
    private final String HIDE_BUTTON_TEXT = "Hide";
    private final String RECOMMENDED_ADD_BUTTON_TEXT = "Add to recommended";
    private final String RECOMMENDED_REMOVE_BUTTON_TEXT = "Remove from recommended";
    @FXML
    public HBox hBoxFilters;
    @FXML
    public ListView<Movie> moviesListView;
    @FXML
    public TextField minDuration;
    @FXML
    public TextField maxDuration;
    @FXML
    public TextField name;
    public Optional<CheckBox> isRecommended = Optional.empty();
    @FXML
    public Pagination pagination;
    private Optional<LoginUser> loginUser = Optional.empty();
    @Autowired
    private MovieService movieService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RecommendationService recommendationService;
    private List<Recommendation> recommendedMovies = new ArrayList<>();

    public static URL getFXML() {
        return MovieController.class.getResource("/fxml/EditMovies.fxml");
    }

    @EventListener
    public void handleLoginEvent(LoginEvent userEvent) {
        var user = (LoginUser) userEvent.getSource();
        loginUser = Optional.of(user);
    }

    @FXML
    void initialize() {

        if (loginUser.isPresent()) {
            var user = loginUser.get();
            permissionService.getPermissionsForUser(user).map((permission -> {
                return permission;
            })).any(permission -> permission.name.equals("RECOMMENDATION")).subscribe(hasPermission -> {
                if (hasPermission) {
                    this.isRecommended = Optional.of(new CheckBox("Recommended"));
                    this.isRecommended.get().setSelected(false);

                    Platform.runLater(() -> {
                        var filterChildren = this.hBoxFilters.getChildren();
                        filterChildren.add(this.isRecommended.get());
                    });
                    this.isRecommended.get().setOnAction(event -> this.refreshList());
                } else {
                    if (this.isRecommended.isPresent()) {
                        this.isRecommended = Optional.empty();
                    }
                    Platform.runLater(() -> {
                        var filterChildren = this.hBoxFilters.getChildren();
                        if (filterChildren.size() > 3) {
                            filterChildren.remove(filterChildren.size() - 1);
                        }
                    });
                }
            });
        }

        this.moviesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        this.moviesListView.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                var currentItemSelected = this.moviesListView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.NONE);

                alert.getButtonTypes().add(new ButtonType(HIDE_BUTTON_TEXT));
                alert.getButtonTypes().add(new ButtonType(DELETE_BUTTON_TEXT));

                if (recommendedMovies.stream().anyMatch(recommendation -> recommendation.getMovie_id() == currentItemSelected.getId())) {
                    alert.getButtonTypes().add(new ButtonType(RECOMMENDED_REMOVE_BUTTON_TEXT));
                } else {
                    alert.getButtonTypes().add(new ButtonType(RECOMMENDED_ADD_BUTTON_TEXT));
                }

                alert.setTitle(currentItemSelected.getName());
                alert.setHeaderText("Description: " + currentItemSelected.getDescription());
                alert.setContentText("duration: " + currentItemSelected.getDuration());

                alert.setResultConverter((bt) -> {
                    if (bt.getText().equals(DELETE_BUTTON_TEXT)) {
                        movieService.deleteMovie(currentItemSelected.getId()).block();
                        refreshList();
                    } else if (bt.getText().equals(RECOMMENDED_ADD_BUTTON_TEXT)) {
                        recommendationService.addRecommendation(new Recommendation(currentItemSelected.getId())).block();
                        refreshList();
                    } else if (bt.getText().equals(RECOMMENDED_REMOVE_BUTTON_TEXT)) {
                        recommendationService.deleteRecommendationByMovieId(currentItemSelected.getId()).block();
                        refreshList();
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
        this.recommendedMovies = recommendationService.getRecomendedMovies().collect(Collectors.toList()).block();
        Optional<Boolean> isRecommendedFilter = this.isRecommended.map(CheckBox::isSelected);
        Optional<String> newNameFilter = Optional.ofNullable(name.getText()).filter(s -> !s.isEmpty());
        Optional<Integer> newMinDuration = textFieldToOptInt(this.minDuration);
        Optional<Integer> newMaxDuration = textFieldToOptInt(this.maxDuration);

        var moviesFilterDTO = new MovieFiltersDTO(newMinDuration, newMaxDuration, newNameFilter, isRecommendedFilter);

        Platform.runLater(() -> {
            var numberPages = Math.floorDiv(
                    movieService.getMovieCountWithFilter(moviesFilterDTO).block(),
                    pagination.getMaxPageIndicatorCount()
            );
            pagination.setPageCount(numberPages > 0 ? numberPages : 1);
            this.moviesListView.setItems(FXCollections.observableArrayList(
                    movieService.getMoviesWithFilterDTO(
                                    moviesFilterDTO,
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
