package com.ou.clinicmanagement;

import com.ou.pojos.Appointment;
import com.ou.pojos.Patient;
import com.ou.pojos.User;
import com.ou.services.AppointmentService;
import com.ou.services.PatientService;
import com.ou.services.TicketService;
import com.ou.services.UserService;
import com.ou.utils.DatePickerConverter;
import com.ou.utils.exceptions.ValidatorException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class AppointmentManagementController implements Initializable {
    @FXML
    public Button deleteBtn;
    @FXML
    public Button createTicketBtn;
    @FXML
    public SearchableComboBox<Patient> patientCB;
    @FXML
    public DatePicker datePicker;
    @FXML
    public Button addAppointmentBtn;
    @FXML
    public SearchableComboBox<User> doctorCB;
    @FXML
    public TableView<Appointment> tblView;
    @FXML
    public Button backBtn;
    @FXML
    public Button refreshBtn;

    private final AppointmentService appointmentService = new AppointmentService();
    private final UserService userService = new UserService();
    private final PatientService patientService = new PatientService();
    private final TicketService ticketService = new TicketService();

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private final ObservableList<User> doctors = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(event -> App.back());
        datePicker.setConverter(new DatePickerConverter());
        addAppointmentBtn.setOnAction(event -> onAddAppointment());
        deleteBtn.setOnAction(event -> onDeleteAppointment());
        refreshBtn.setOnAction(event -> loadAppointments());
        createTicketBtn.setOnAction(event -> onCreateTicket());
        loadAppointments();
        initTblView();
        initPatientCB();
        initDoctorCB();
    }

    private void initTblView() {
        TableColumn<Appointment, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Appointment, Long> patientIdCol = new TableColumn<>("Mã bệnh nhân");
        patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, String> patientIdCodeCol = new TableColumn<>("CCCD");
        patientIdCodeCol.setCellValueFactory(t -> new SimpleStringProperty(t.getValue().getPatient().getIdCode()));
        TableColumn<Appointment, String> nameCol = new TableColumn<>("Họ tên bệnh nhân");
        nameCol.setCellValueFactory(t -> new SimpleStringProperty(String.join(" ", Stream.of(t.getValue().getPatient().getLastName(), t.getValue().getPatient().getMiddleName(), t.getValue().getPatient().getFirstName()).filter(st -> st != null && !st.isBlank()).toList())));
        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Ngày hẹn");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, Boolean> isCheckinCol = new TableColumn<>("Trạng thái");
        isCheckinCol.setCellValueFactory(new PropertyValueFactory<>("checkin"));
        isCheckinCol.setCellFactory(t -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Đã tiếp nhận" : "Chưa được tiếp nhận");
                }
            }
        });
        tblView.getColumns().addAll(Arrays.asList(idCol, patientIdCol, patientIdCodeCol, nameCol, dateCol, isCheckinCol));
        tblView.setItems(appointments);
    }

    private void loadAppointments() {
        try {
            appointments.setAll(appointmentService.getAppointments());
        } catch (SQLException e) {
        }
    }

    private void initPatientCB() {
        patientCB.setCellFactory((lv) -> new ListCell<>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s %s %s - %s", item.getLastName(), item.getMiddleName(), item.getFirstName(), item.getIdCode()));
                }
            }
        });

        patientCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Patient item) {
                if (item == null) {
                    return "";
                } else {
                    return String.format("%s %s %s - %s", item.getLastName(), item.getMiddleName(), item.getFirstName(), item.getIdCode());
                }
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });

        patientCB.setItems(patients);
        try {
            patients.addAll(patientService.getPatients());
        } catch (Exception ignored) {
        }
    }

    private void initDoctorCB() {
        doctorCB.setCellFactory((lv) -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - ID: %d", item.getFirstName(), item.getId()));
                }
            }
        });

        doctorCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(User item) {
                if (item == null) {
                    return "";
                } else {
                    return String.format("%s - ID: %d", item.getFirstName(), item.getId());
                }
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        doctorCB.setItems(doctors);

        try {
            doctors.addAll(userService.getAllUsersByRole("DOCTOR"));
        } catch (Exception ignored) {
        }
    }

    private void onAddAppointment() {
        Patient patient = patientCB.getSelectionModel().getSelectedItem();
        LocalDate appointmentDate = datePicker.getValue();
        if (patient == null || appointmentDate == null) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Bệnh nhân và ngày hẹn không được trống", null, null);
            return;
        }

        try {
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setPatientId(patient.getId());
            appointmentService.add(appointment);
            appointments.add(appointment);
            patientCB.setValue(null);
            datePicker.setValue(null);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
        }
    }

    private void onDeleteAppointment() {
        Appointment appointment = tblView.getSelectionModel().getSelectedItem();
        if (appointment == null) return;
        try {
            appointmentService.delete(appointment.getId());
            appointments.remove(appointment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onCreateTicket() {
        TableSelectionModel<Appointment> selectionModel = tblView.getSelectionModel();
        Appointment appointment = selectionModel.getSelectedItem();
        User doctor = doctorCB.getSelectionModel().getSelectedItem();
        if (appointment == null || doctor == null) return;
        try {
            ticketService.createTicket(doctor.getId(), appointment.getPatientId(), appointment.getId());
            appointmentService.updateStatus(appointment.getId(), true);
            appointment.setCheckin(true);
            tblView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
        }
    }
}
