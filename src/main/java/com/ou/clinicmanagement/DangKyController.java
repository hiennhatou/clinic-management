package com.ou.clinicmanagement;

import com.ou.pojos.Patient;
import com.ou.services.UserService;
import com.ou.utils.DatePickerConverter;
import com.ou.utils.exceptions.ValidatorException;
import com.ou.utils.userbuilder.PatientBuilder;
import com.ou.utils.userbuilder.UserBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class DangKyController implements Initializable {
    UserService userService = new UserService();

    @FXML
    private TextField username;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField middleName;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private TextField idCode;
    @FXML
    private Button closeBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private Text toLogin;
    @FXML
    private Text errorMsg;
    @FXML
    private VBox loader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateOfBirth.setConverter(new DatePickerConverter());
        signUpBtn.setOnAction(this::onLogin);
        toLogin.setOnMouseClicked(this::onMoveToLogin);
    }

    private void onLogin(ActionEvent actionEvent) {
        Thread.ofVirtual().start(() -> {
            try {
                loader.setVisible(true);
                Patient patient = validateFields();

                UserBuilder userBuilder = new PatientBuilder(patient);
                Thread a = Thread.ofVirtual().start(() -> userService.createUser(userBuilder));
                a.join();
                App.showAlert(Alert.AlertType.INFORMATION, "Đăng ký", "Đăng ký thành công", null, e -> App.moveScene("dang-nhap.fxml"));
            } catch (ValidatorException e) {
                errorMsg.setText(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loader.setVisible(false);
            }
        });
    }

    private void onMoveToLogin(MouseEvent event) {
        App.moveScene("dang-nhap.fxml");
    }

    private Patient validateFields() {
        errorMsg.setText("");
        String username = this.username.getText();
        String password = this.passwordField.getText();
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String middleName = this.middleName.getText();
        LocalDate dateOfBirth = this.dateOfBirth.getValue();
        String idCode = this.idCode.getText();

        if (dateOfBirth == null) {
            throw new ValidatorException("Ngày sinh không hợp lệ", "dateOfBirth");
        }

        if (username == null || password == null || firstName == null || middleName == null || idCode == null ||
            username.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            throw new ValidatorException("Vui lòng nhập đầy đủ thông tin.", "username");
        }

        Pattern idCodePattern = Pattern.compile("^[0-9]{12}$");
        if (!idCodePattern.matcher(idCode).matches()) {
            throw new ValidatorException("Số CCCD không hợp lệ", "idCode");
        }

        Pattern usernamePattern = Pattern.compile("^[A-z]([A-z0-9]){2,15}$");
        if (!usernamePattern.matcher(username).matches()) {
            throw new ValidatorException("Tên tài khoản phải bắt đầu bằng chữ cái. Tên tài khoản chỉ được chứa chữ cái và chữ số, không phân biệt chữ thường và chữ hoa. Độ dài từ 3 - 16.", "username");
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?\\\\|]).{8,30}$");
        if (!passwordPattern.matcher(password).matches()) {
            throw new ValidatorException("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số, và 1 ký tự đặc biệt. Độ dài 8 - 30", "password");
        }

        Patient patient = new Patient();
        patient.setFirstName(firstName.trim());
        patient.setLastName(lastName.trim());
        patient.setMiddleName(middleName.trim());
        patient.setIdCode(idCode);
        patient.setUsername(username.trim().toLowerCase());
        patient.setPassword(password);
        patient.setBirthday(dateOfBirth);
        patient.setRole("PATIENT");
        return patient;
    }
}
