package com.ou.clinicmanagement;

import com.ou.services.AuthService;
import com.ou.utils.exceptions.AuthFail;
import com.ou.utils.exceptions.ValidatorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

public class LoginController implements Initializable {
    AuthService authService = new AuthService();

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginbtn;

    @FXML
    private Text errorMsg;

    @FXML
    private VBox loader;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginbtn.setOnAction(this::onLogin);
    }

    private void onLogin(ActionEvent actionEvent) {
        loader.setVisible(true);
        errorMsg.setText("");
        Thread.ofVirtual().start(() -> {
            try {
                String username = this.username.getText();
                String password = this.password.getText();

                if (username == null || password == null || username.isEmpty() || password.isEmpty())
                    throw new ValidatorException("Vui lòng nhập đầy đủ thông tin", "general");

                authService.authenticate(username, password);
                App.moveScene("welcome.fxml", false);
            } catch (ValidatorException | AuthFail e) {
                errorMsg.setText(e.getMessage());
            } catch (Exception e) {
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi hệ thống", null, null);
                Logger.getLogger(App.class.getName()).log(Level.WARNING, e.getMessage(), e);
            } finally {
                loader.setVisible(false);
            }
        });
    }
}