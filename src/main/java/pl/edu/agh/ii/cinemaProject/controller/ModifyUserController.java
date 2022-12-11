package pl.edu.agh.ii.cinemaProject.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.agh.ii.cinemaProject.db.RoleDao;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import pl.edu.agh.ii.cinemaProject.model.LoginUser;
import pl.edu.agh.ii.cinemaProject.model.Role;

import java.net.URL;
import java.util.Map;
import java.util.function.Function;

@Controller
public class ModifyUserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;

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

    private Map<Long, Role> rolesMap;

    public static URL getFXML() {
        return ModifyUserController.class.getResource("/fxml/ModifyUser.fxml");
    }

    @FXML
    private void initialize() {
        rolesMap = roleDao.findAll().collectMap(Role::getId, Function.identity()).block();
        mainTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
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
        userDao.findAll().toStream().forEach(user -> {
            userRole.setCellValueFactory((data) -> new SimpleObjectProperty<>(rolesMap.get(data.getValue().getRoleId())));
            mainTableView.getItems().add(user);
        });
    }
}
