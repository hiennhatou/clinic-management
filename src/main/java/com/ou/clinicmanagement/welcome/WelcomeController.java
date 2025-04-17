package com.ou.clinicmanagement.welcome;

import com.ou.clinicmanagement.App;
import com.ou.clinicmanagement.RootStack;
import com.ou.pojos.User;
import com.ou.services.AuthService;
import com.ou.services.UserService;
import com.ou.utils.exceptions.AuthFail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WelcomeController implements Initializable {
    private User currentUser;

    @FXML
    public Text username;

    @FXML
    private AnchorPane container;

    @FXML
    private Button logout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUI();
        username.setText(currentUser.getFirstName());
        logout.setOnAction(this::onLogout);
    }

    private void loadUI() {
        try {
            currentUser = AuthService.getCurrentUser();
            if (currentUser == null) throw new AuthFail("");
            String page = getPage(currentUser);
            Parent welcomeParent = App.getFXMLLoader(page).load();


            container.getChildren().setAll(welcomeParent);
        } catch (Exception e) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp sự cố", null, null);
            Logger.getLogger(WelcomeController.class.getName()).log(Level.SEVERE, "Error", e);
            if (e instanceof AuthFail) {
                RootStack.clear();
                App.moveScene("login.fxml", false);
            }
        }
    }

    private static String getPage(User currentUser) {
        return switch (currentUser.getRole()) {
            case "ADMIN" -> "welcome-admin.fxml";
            case "DOCTOR" -> "welcome-doctor.fxml";
            case "PHARMACIST" -> "welcome-pharmacist.fxml";
            case "STAFF" -> "welcome-staff.fxml";
            default -> null;
        };
    }

    private void onLogout(ActionEvent actionEvent) {
        try {
            AuthService.logout();
        } catch (Exception ignored) {

        } finally {
            RootStack.clear();
            App.moveScene("login.fxml", false);
        }
    }
}
