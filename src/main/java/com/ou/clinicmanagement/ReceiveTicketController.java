package com.ou.clinicmanagement;

import com.ou.pojos.Patient;
import com.ou.pojos.Prescription;
import com.ou.pojos.Ticket;
import com.ou.services.AuthService;
import com.ou.services.TicketService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ReceiveTicketController implements Initializable {
    @FXML
    public Button backBtn;
    @FXML
    public Button refreshBtn;
    @FXML
    public TableView<Ticket> tblView;

    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private final TicketService ticketService = new TicketService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(e -> App.back());
        refreshBtn.setOnAction(e -> loadTickets());
        initTblView();
    }

    private void initTblView() {
        TableColumn<Ticket, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Ticket, String> nameCol = new TableColumn<>("Họ tên bệnh nhân");
        nameCol.setCellValueFactory(t -> {
            Patient patient = t.getValue().getPatient();
            String name = String.format("%s %s", patient.getLastName(), patient.getFirstName());
            return new SimpleStringProperty(name);
        });

        TableColumn<Ticket, LocalDateTime> createdDate = new TableColumn<>("Ngày tạo");
        createdDate.setCellValueFactory(new PropertyValueFactory<>("createdOn"));
        createdDate.setCellFactory(c -> new TableCell<Ticket, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy").format(item));
                }
            }
        });

        TableColumn<Ticket, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(t -> new SimpleStringProperty(Ticket.getReadableStatus(t.getValue().getStatus())));

        tblView.setRowFactory(tr -> {
            TableRow<Ticket> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Ticket ticket = row.getItem();
                if (event.getClickCount() == 2 && ticket != null) {
                    FXMLLoader loader = App.getFXMLLoader("ticket-detail.fxml");
                    loader.setControllerFactory(t -> {
                        TicketDetailController controller = new TicketDetailController();
                        controller.setId(ticket.getId());
                        return controller;
                    });
                    RootStack.push(App.getScene().getRoot());
                    try {
                        App.getScene().setRoot(loader.load());
                    } catch (IOException e) {

                    }
                }
            });
            return row;
        });
        tblView.getColumns().addAll(Arrays.asList(idCol, nameCol, statusCol, createdDate));
        tblView.setItems(tickets);
        loadTickets();
    }

    private void loadTickets() {
        try {
            if (AuthService.getCurrentUser() != null)
                tickets.setAll(ticketService.getTicketsByDoctor(AuthService.getCurrentUser().getId()));
        } catch (SQLException e) {
        }
    }
}
