package pl.edu.agh.ii.cinemaProject.controller;

import io.vavr.Function1;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
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
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.vavr.API.Try;


@Controller
public class MovieController {
    private final String DELETE_BUTTON_TEXT = "Delete";
    private final String RECOMMENDED_ADD_BUTTON_TEXT = "Add to recommended";
    private final String RECOMMENDED_REMOVE_BUTTON_TEXT = "Remove from recommended";

    @FXML
    private HBox hBoxFilters;
    @FXML
    private ListView<Movie> moviesListView;
    @FXML
    private TextField minDuration;
    @FXML
    private TextField maxDuration;
    @FXML
    private TextField name;
    @FXML
    private Pagination pagination;

    @Autowired
    private MovieService movieService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RecommendationService recommendationService;

    private List<Recommendation> recommendedMovies = new ArrayList<>();
    private Optional<LoginUser> loginUser = Optional.empty();
    private Optional<CheckBox> isRecommended = Optional.empty();

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
        loginUser.ifPresent(user ->
                permissionService.getPermissionsForUser(user).any(permission ->
                        permission.name.equals("RECOMMENDATION")).subscribe(hasPermission -> {
                    if (hasPermission) {
                        this.isRecommended = Optional.of(new CheckBox("Recommended"));
                        this.isRecommended.get().setSelected(false);
                        Platform.runLater(() -> this.hBoxFilters.getChildren().add(this.isRecommended.get()));
                        this.isRecommended.get().setOnAction(event -> this.refreshList());
                    } else {
                        this.isRecommended = Optional.empty();
                        Platform.runLater(() -> {
                            var filterChildren = this.hBoxFilters.getChildren();
                            if (filterChildren.size() > 3) {
                                filterChildren.remove(filterChildren.size() - 1);
                            }
                        });
                    }
                })
        );


        this.moviesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        this.moviesListView.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                var currentItemSelected = this.moviesListView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.NONE);
                Window window = alert.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> alert.hide());

                alert.getButtonTypes().add(new ButtonType(DELETE_BUTTON_TEXT));
                alert.getButtonTypes().add(
                        recommendedMovies.stream().anyMatch(rec -> rec.getMovie_id() == currentItemSelected.getId())
                                ? new ButtonType(RECOMMENDED_REMOVE_BUTTON_TEXT)
                                : new ButtonType(RECOMMENDED_ADD_BUTTON_TEXT)
                );
                alert.setTitle(currentItemSelected.getName());
                alert.setHeaderText("Description: " + currentItemSelected.getDescription());
                alert.setContentText("duration: " + currentItemSelected.getDuration());
                alert.setResultConverter((bt) -> {
                    (switch (bt.getText()) {
                        case DELETE_BUTTON_TEXT -> Function1.of(movieService::deleteMovie);
                        case RECOMMENDED_ADD_BUTTON_TEXT ->
                                Function1.of(Recommendation::fromMovieId).andThen(Function1.of(recommendationService::addRecommendation));
                        case RECOMMENDED_REMOVE_BUTTON_TEXT ->
                                Function1.of(recommendationService::deleteRecommendationByMovieId);
                        default -> throw new IllegalStateException("Unexpected value: " + bt.getText());
                    }).andThen(Mono::block).andThen((v) -> refreshList()).apply(currentItemSelected.getId());
                    return bt;
                });

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/Alert.css")).toExternalForm());
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

        this.pagination.setMaxPageIndicatorCount(10);
        this.pagination.setPageFactory((pageIndex) -> {
            refreshList();
            return new VBox();
        });

        refreshList();
    }

    private Void refreshList() {
        this.recommendedMovies = recommendationService.getRecommendedMovies().collectList().block();
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
        return null;
    }

    private Optional<Integer> textFieldToOptInt(TextField textField) {
        return Try(() -> Integer.parseInt(textField.getText()))
                .toEither()
                .fold((th) -> Optional.empty(), Optional::of);
    }
}
