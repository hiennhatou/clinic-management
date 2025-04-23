package com.ou.clinicmanagement;

import com.ou.pojos.Patient;
import com.ou.pojos.Ticket;
import com.ou.pojos.User;
import com.ou.services.PatientService;
import com.ou.services.TicketService;
import com.ou.services.UserService;
import com.ou.utils.exceptions.ValidatorException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketManagementController implements Initializable {
    UserService userService = new UserService();
    PatientService patientService = new PatientService();
    TicketService ticketService = new TicketService();

    ObservableList<User> doctors = FXCollections.observableArrayList();
    ObservableList<Patient> patients = FXCollections.observableArrayList();
    ObservableList<Ticket> tickets = FXCollections.observableArrayList();

    @FXML
    public SearchableComboBox<Patient> idCodeCB;
    @FXML
    public Button backBtn;
    @FXML
    public TableView<Ticket> tblView;
    @FXML
    public ComboBox<User> doctorCB;
    @FXML
    public Button acceptBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backBtn.setOnAction(event -> App.back());
        acceptBtn.setOnAction(event -> addTicket());
        initDoctorComboBox();
        initPatientComboBox();
        initTableView();
    }

    private void initDoctorComboBox() {
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

    private void initPatientComboBox() {
        idCodeCB.setCellFactory((lv) -> new ListCell<>() {
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

        idCodeCB.setConverter(new StringConverter<>() {
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

        idCodeCB.setItems(patients);
        try {
            patients.addAll(patientService.getPatients());
        } catch (Exception ignored) {
        }
    }

    private void addTicket() {
        Patient patient = idCodeCB.getValue();
        User user = doctorCB.getValue();

        if (patient == null || user == null) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu!", "Nhập đầy đủ thông tin", null, null);
            return;
        }

        try {
            Ticket ticket = ticketService.createTicket(user.getId(), patient.getId());
            ticket.setDoctor(user);
            ticket.setPatient(patient);
            ticket.setCreatedOn(LocalDateTime.now());
            ticket.setStatus("created");
            tickets.add(ticket);
            doctorCB.setValue(null);
            idCodeCB.setValue(null);
        } catch (SQLException e) {
            Logger.getLogger(TicketManagementController.class.getName()).log(Level.SEVERE, null, e);
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu!", "Hệ thống gặp sự cố", null, null);
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
        }
    }

    private void initTableView() {
        tblView.setEditable(true);
        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Ticket, User> doctorCol = new TableColumn<>("Bác sĩ");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        doctorCol.setOnEditCommit(event -> {
            Ticket ticket = event.getRowValue();
            try {
                ticketService.updateDoctor(ticket.getId(), event.getNewValue().getId());
                ticket.setDoctor(event.getNewValue());
            } catch (SQLException e) {

            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });
        doctorCol.setCellFactory(t -> {
            ComboBoxTableCell<Ticket, User> tableCell = new ComboBoxTableCell<>();
            tableCell.setConverter(new StringConverter<>() {
                @Override
                public String toString(User object) {
                    return String.format("%s %s - ID: %d", object.getLastName(), object.getFirstName(), object.getId());
                }

                @Override
                public User fromString(String string) {
                    return tableCell.getItem();
                }
            });
            tableCell.getItems().addAll(doctors);
            return tableCell;
        });

        TableColumn<Ticket, String> patientCol = new TableColumn<>("Bệnh nhân");
        patientCol.setCellValueFactory(t -> new SimpleStringProperty(String.format("%s %s (ID: %d)", t.getValue().getPatient().getLastName(), t.getValue().getPatient().getFirstName(), t.getValue().getPatientId())));

        TableColumn<Ticket, String> createdDateCol = new TableColumn<>("Ngày tiếp nhận");
        createdDateCol.setCellValueFactory(t -> new SimpleStringProperty(t.getValue().getCreatedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

        TableColumn<Ticket, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(t -> new SimpleStringProperty(t.getValue().getStatus()));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return Ticket.getReadableStatus(object);
            }

            @Override
            public String fromString(String string) {
                return switch (string) {
                    case "Vừa được tạo" -> "created";
                    case "Đã tiếp nhận" -> "checked_in";
                    case "Đã hoàn thành" -> "done";
                    default -> null;
                };
            }
        }, "created", "checked_in", "done"));
        statusCol.setOnEditCommit(event -> {
            Ticket ticket = event.getRowValue();
            try {
                ticketService.updateStatus(ticket.getId(), ticket.getPatientId(), event.getNewValue());
                ticket.setStatus(event.getNewValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ValidatorException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
                tblView.refresh();
            }
        });

        tblView.getColumns().addAll(Arrays.asList(idCol, doctorCol, patientCol, createdDateCol, statusCol));
        tblView.setItems(tickets);
        try {
            tickets.addAll(ticketService.getAllTickets());
        } catch (SQLException ignored) {
        }
    }
}
