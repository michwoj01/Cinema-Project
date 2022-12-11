package pl.edu.agh.ii.cinemaProject.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Role;
import pl.edu.agh.ii.cinemaProject.service.LoginService;
import pl.edu.agh.ii.cinemaProject.service.RoleService;

import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Controller
public class ModifyUserController {
    @Autowired
    private RoleService roleService;

    @FXML
    private TableView<LoginUser> mainTableView;
    @FXML
    private TableColumn<LoginUser, String> userFirstName;
    @FXML
    private TableColumn<LoginUser, String> userLastName;
    @FXML
    private TableColumn<LoginUser, String> userEmail;
    @FXML
    private TableColumn<LoginUser, Role> userRole;
    @FXML
    private Button deleteButton;

    @Autowired
    private LoginService loginService;

    private Map<Long, Role> rolesMap;

    public static URL getFXML() {
        return ModifyUserController.class.getResource("/fxml/ModifyUser.fxml");
    }

    @FXML
    private void initialize() {
        rolesMap = roleService.getAllRoles().collectMap(Role::getId, Function.identity()).block();

        mainTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRole.setCellValueFactory((data) -> new SimpleObjectProperty<>(rolesMap.get(data.getValue().getRoleId())));

        var roleList = FXCollections.observableArrayList(rolesMap.values());
        var stringConverter = new StringConverter<Role>() {
            @Override
            public String toString(Role object) {
                return object.getName();
            }

            @Override
            public Role fromString(String string) {
                return rolesMap.values().stream().filter((role) -> role.getName().equals(string)).findAny().get();
            }
        };
        userRole.setCellFactory(ComboBoxTableCell.forTableColumn(stringConverter, roleList));
        userRole.setOnEditCommit(e -> performUpdate(e, (user, role) -> {
            user.setRoleId(role.getId());
            return null;
        }));

        userEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        userEmail.setOnEditCommit(e -> performUpdate(e, (user, newValue) -> {
            user.setEmail(newValue);
            return null;
        }));

        userFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        userFirstName.setOnEditCommit(e -> performUpdate(e, (user, newValue) -> {
            user.setFirstName(newValue);
            return null;
        }));

        userLastName.setCellFactory(TextFieldTableCell.forTableColumn());
        userLastName.setOnEditCommit(e -> performUpdate(e, (user, newValue) -> {
            user.setLastName(newValue);
            return null;
        }));

        deleteButton.disableProperty().bind(Bindings.isEmpty(mainTableView.getSelectionModel().getSelectedItems()));

        loginService.getAll().toStream().forEach(user -> mainTableView.getItems().add(user));
    }

    private <T> void performUpdate(TableColumn.CellEditEvent<LoginUser, T> cellEditEvent, BiFunction<LoginUser, T, Void> updateFunction) {
        var pair = getOldAndNewValue(cellEditEvent);

        updateFunction.apply(pair.getFirst(), pair.getSecond());

        loginService.createOrUpdateUser(pair.getFirst()).fold((error) -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Error while updating user");
            alert.setContentText("Error: " + error);

            alert.showAndWait();
            return null;
        }, (user) -> {
            var viewModel = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
            viewModel.setId(user.getId());
            updateFunction.apply(viewModel, cellEditEvent.getNewValue());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OK");
            alert.setHeaderText("Succesfully saved user");

            alert.showAndWait();
            return null;
        });

    }

    private <T> Pair<LoginUser, T> getOldAndNewValue(TableColumn.CellEditEvent<LoginUser, T> event) {
        var oldValue = event.getTableView().getItems().get(event.getTablePosition().getRow());
        var changedValue = event.getNewValue();

        return Pair.of(oldValue, changedValue);
    }

    public void handleDeleteUserAction(ActionEvent actionEvent) {
        mainTableView.getSelectionModel().getSelectedItems().forEach(user -> {
            if (user.getId() != 0) {
                loginService.deleteUser(user.getId()).fold((error) -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Error while deleting user");
                    alert.setContentText("Error: " + error);

                    alert.showAndWait();
                    return null;
                }, (__) -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("OK");
                    alert.setHeaderText("Succesfully deleted user");
                    alert.setContentText("User: " + user);

                    alert.showAndWait();
                    return null;
                });
            }
            mainTableView.getItems().remove(user);
        });

    }

    public void handleAddUserAction(ActionEvent actionEvent) {
        var newUser = new LoginUser();
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setRoleId(3);
        mainTableView.getItems().add(newUser);
    }
}
