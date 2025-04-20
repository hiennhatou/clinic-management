package com.ou.clinicmanagement;

import com.ou.pojos.Patient;
import com.ou.services.PatientService;
import com.ou.utils.DatePickerConverter;
import com.ou.utils.exceptions.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientManagementController implements Initializable {
    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private final PatientService patientService = new PatientService();

    @FXML
    public TextField lastNameTF;
    @FXML
    public TextField middleNameTF;
    @FXML
    public TextField firstNameTF;
    @FXML
    public TextField idCodeTF;
    @FXML
    public DatePicker birthdayDatePicker;
    @FXML
    public Button addPatientBtn;
    @FXML
    public TableView<Patient> tblView;
    @FXML
    public Button backBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTblView();
        birthdayDatePicker.setConverter(new DatePickerConverter());
        backBtn.setOnAction(event -> App.back());
        addPatientBtn.setOnAction(event -> addPatient());
    }

    private void initTblView() {
        TableColumn<Patient, String> firstNameCol = new TableColumn<>("Tên");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(v -> {
            Patient patient = v.getRowValue();
            try {
                patient.setFirstName(patientService.updateFirstName(v.getNewValue(), patient.getId()));
            } catch (SQLException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Hệ thống gặp lỗi", null, null);
                Logger.getLogger(PatientManagementController.class.getName()).log(Level.SEVERE, null, e);
                tblView.refresh();
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });

        TableColumn<Patient, String> lastNameCol = new TableColumn<>("Họ");
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setOnEditCommit(v -> {
            Patient patient = v.getRowValue();
            try {
                patient.setLastName(patientService.updateLastName(v.getNewValue(), patient.getId()));
            } catch (SQLException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Hệ thống gặp lỗi", null, null);
                Logger.getLogger(PatientManagementController.class.getName()).log(Level.SEVERE, null, e);
                tblView.refresh();
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });

        TableColumn<Patient, String> middleNameCol = new TableColumn<>("Tên đệm");
        middleNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        middleNameCol.setOnEditCommit(v -> {
            Patient patient = v.getRowValue();
            try {
                patient.setMiddleName(patientService.updateMiddleName(v.getNewValue(), patient.getId()));
            } catch (SQLException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Hệ thống gặp lỗi", null, null);
                Logger.getLogger(PatientManagementController.class.getName()).log(Level.SEVERE, null, e);
                tblView.refresh();
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });

        TableColumn<Patient, String> idCodeCol = new TableColumn<>("CCCD");
        idCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCodeCol.setCellValueFactory(new PropertyValueFactory<>("idCode"));
        idCodeCol.setOnEditCommit(v -> {
            Patient patient = v.getRowValue();
            try {
                patient.setIdCode(patientService.updateIdCode(v.getNewValue(), patient.getId()));
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062)
                    App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Số CCCD đã tồn tại", null, null);
                else
                    Logger.getLogger(PatientManagementController.class.getName()).log(Level.SEVERE, null, e);
                tblView.refresh();
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });

        TableColumn<Patient, LocalDate> birthdayCol = new TableColumn<>("Ngày sinh");
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        birthdayCol.setCellFactory(v -> {
            TextFieldTableCell<Patient, LocalDate> tblCell = new TextFieldTableCell<Patient, LocalDate>();
            tblCell.setConverter(new DatePickerConverter());
            return tblCell;
        });
        birthdayCol.setComparator(LocalDate::compareTo);
        birthdayCol.setOnEditCommit(v -> {
            Patient patient = v.getRowValue();
            try {
                patient.setBirthday(patientService.updateBirthday(v.getNewValue(), patient.getId()));
            } catch (SQLException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Hệ thống gặp lỗi", null, null);
                Logger.getLogger(PatientManagementController.class.getName()).log(Level.SEVERE, null, e);
                tblView.refresh();
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });

        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setEditable(false);

        tblView.getColumns().addAll(Arrays.asList(idCol, idCodeCol, lastNameCol, middleNameCol, firstNameCol, birthdayCol));
        tblView.setItems(patients);
        patients.addAll(patientService.getPatients());
    }

    private void addPatient() {
        Patient patient = new Patient(idCodeTF.getText(), firstNameTF.getText(), lastNameTF.getText(), middleNameTF.getText(), birthdayDatePicker.getValue());
        try {
            patientService.insertPatient(patient);
            patients.add(patient);
            clearFields();
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Bệnh nhân chưa được lưu!", e.getMessage(), null, null);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062)
                App.showAlert(Alert.AlertType.ERROR, "Bệnh nhân chưa được lưu!", "Số CCCD đã tồn tại", null, null);
            else {
                Logger.getLogger(PatientManagementController.class.getName()).log(Level.SEVERE, null, e);
                App.showAlert(Alert.AlertType.ERROR, "Bệnh nhân chưa được lưu!", "Hệ thống gặp sự cố", null, null);
            }
        }
    }

    private void clearFields() {
        lastNameTF.clear();
        middleNameTF.clear();
        firstNameTF.clear();
        idCodeTF.clear();
        birthdayDatePicker.setValue(null);
    }
}
