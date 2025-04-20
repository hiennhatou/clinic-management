package com.ou.clinicmanagement;

import com.ou.pojos.User;
import com.ou.services.UserService;
import com.ou.utils.exceptions.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementController implements Initializable {
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField lastNameTextField;
    @FXML
    public TextField usernameTextField;
    @FXML
    public TextField middleNameTextField;
    @FXML
    public ChoiceBox<String> roleChoiceBox;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Button addUserBtn;
    @FXML
    public Button deleteBtn;
    @FXML
    public TableView<User> tblView;
    @FXML
    public Button backToMainMenu;

    UserService userService = new UserService();
    ObservableList<User> users = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addUserBtn.setOnAction(event -> onAddUser());
        deleteBtn.setOnAction(event -> deleteUsers());
        roleChoiceBox.setItems(FXCollections.observableList(List.of("ADMIN", "DOCTOR", "PHARMACIST", "STAFF")));
        backToMainMenu.setOnAction(event -> App.back());
        initUserList();
    }

    private void initUserList() {
        TableColumn<User, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setEditable(false);

//        Tên (TextField)
        TableColumn<User, String> firstnameCol = new TableColumn<>("Tên");
        firstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstnameCol.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        firstnameCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            try {
                user.setFirstName(userService.updateFirstName(event.getNewValue(), user.getId()));
            } catch (SQLException e) {
                Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Mật khẩu chưa được lưu", e.getMessage(), "Mật khẩu không đủ mạnh!", null);
            }
        });

//        Họ (TextField)
        TableColumn<User, String> lastnameCol = new TableColumn<>("Họ");
        lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastnameCol.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        lastnameCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            try {
                user.setLastName(userService.updateLastName(event.getNewValue(), user.getId()));
            } catch (SQLException e) {
                Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
            }
        });

//        Tên đệm (TextField)
        TableColumn<User, String> middleNameCol = new TableColumn<>("Đệm");
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        middleNameCol.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        middleNameCol.setOnEditCommit(event -> {
            User user = event.getTableView().getItems().get(event.getTablePosition().getRow());
            try {
                user.setMiddleName(userService.updateMiddleName(event.getNewValue(), user.getId()));
            } catch (SQLException e) {
                Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
            }
        });

//        Tên đăng nhập (TextField)
        TableColumn<User, String> usernameCol = new TableColumn<>("Tên đăng nhập");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        usernameCol.setOnEditCommit(event -> {
            User user = event.getTableView().getItems().get(event.getTablePosition().getRow());
            try {
                user.setUsername(userService.updateUsername(event.getNewValue(), user.getId()));
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    App.showAlert(Alert.AlertType.ERROR, "Tên đăng nhập chưa được lưu", "Tên đăng nhập đã tồn tại", null, null);
                    tblView.refresh();
                    return;
                }
                Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Tên đăng nhập chưa được lưu", e.getMessage(), null, null);
            }
        });

//        Vai trò (ChoiceBox)
        TableColumn<User, String> roleCol = new TableColumn<>("Vai trò");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList("ADMIN", "DOCTOR", "PHARMACIST", "STAFF")));
        roleCol.setOnEditCommit(event -> {
            User user = event.getTableView().getItems().get(event.getTablePosition().getRow());
            try {
                user.setRole(userService.updateRole(event.getNewValue(), user.getId()));
            } catch (SQLException e) {
                Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Vai trò chưa được lưu", e.getMessage(), null, null);
            }
        });

//        Mật khẩu (TextField)
        TableColumn<User, String> passwordCol = new TableColumn<>("Mật khẩu");
        passwordCol.setCellFactory(tbc -> new TextFieldTableCell<User, String>() {
            {
                setConverter(new StringConverter<>() {
                    @Override
                    public String fromString(String s) {
                        return s;
                    }

                    @Override
                    public String toString(String s) {
                        return isEditing() ? "" : "********";
                    }
                });
            }

            @Override
            public void commitEdit(String s) {
                super.commitEdit(s);
                User user = getTableView().getItems().get(getIndex());
                try {
                    userService.updatePassword(s, user.getId());
                } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
                    App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
                } catch (ValidatorException e) {
                    App.showAlert(Alert.AlertType.ERROR, "Mật khẩu chưa được lưu", e.getMessage(), "Mật khẩu không đủ mạnh!", null);
                }
            }
        });

        tblView.getColumns().addAll(Arrays.asList(idCol, firstnameCol, lastnameCol, middleNameCol, usernameCol, roleCol, passwordCol));
        tblView.setItems(users);
        try {
            users.addAll(userService.getAllUsers());
        } catch (SQLException ignored) {}
    }

    private void onAddUser() {
        try {
            User user = new User();
            user.setFirstName(nameTextField.getText());
            user.setLastName(lastNameTextField.getText());
            user.setMiddleName(middleNameTextField.getText());
            user.setUsername(usernameTextField.getText());
            user.setRole(roleChoiceBox.getValue());
            user.setPassword(passwordInput.getText());
            userService.createUser(user);
            clearInputFields();
            users.add(user);
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Tài khoản chưa được lưu", e.getMessage(), null, null);
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, e);
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
        }
    }

    private void deleteUsers() {
        User user = tblView.getSelectionModel().getSelectedItem();
        if (user == null) return;
        try {
            userService.deleteUser(user.getId());
            users.remove(user);
        } catch (SQLException e) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
        }
    }

    private void clearInputFields() {
        nameTextField.setText("");
        lastNameTextField.setText("");
        middleNameTextField.setText("");
        usernameTextField.setText("");
        passwordInput.setText("");
        roleChoiceBox.setValue(null);
    }
}
