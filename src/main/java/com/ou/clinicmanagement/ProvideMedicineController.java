package com.ou.clinicmanagement;

import com.ou.pojos.Prescription;
import com.ou.services.PrescriptionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ProvideMedicineController implements Initializable {
    @FXML
    public TableView<Prescription> tblView;
    @FXML
    public Button refreshBtn;
    @FXML
    public Button backBtn;

    private final PrescriptionService prescriptionService = new PrescriptionService();

    private final ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(event -> App.back());
        refreshBtn.setOnAction(event -> loadPrescriptions());
        initTblView();
        loadPrescriptions();
    }

    private void loadPrescriptions() {
        try {
            prescriptions.setAll(prescriptionService.getRequiredPrescriptions());
        } catch (SQLException e) {}
    }

    private void initTblView() {
        TableColumn<Prescription, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Prescription, String> patientIdCol = new TableColumn<>("Mã BN");
        patientIdCol.setCellValueFactory(t -> new SimpleStringProperty(String.valueOf(t.getValue().getTicket().getPatient().getId())));
        TableColumn<Prescription, String> nameCol = new TableColumn<>("Họ tên BN");
        nameCol.setCellValueFactory(t -> {
            Prescription value = t.getValue();
            String name = String.join(" ", Stream.of(value.getTicket().getPatient().getLastName(), value.getTicket().getPatient().getMiddleName(), value.getTicket().getPatient().getFirstName()).filter(st -> st != null && !st.isBlank()).toList());
            return new SimpleStringProperty(name);
        });
        tblView.setRowFactory(tv -> {
            TableRow<Prescription> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Prescription prescription = row.getItem();
                    try {
                        FXMLLoader loader = App.getFXMLLoader("medicine-providence-detail.fxml");
                        loader.setControllerFactory(v -> {
                            MedicineProvidenceDetailController controller = new MedicineProvidenceDetailController();
                            controller.setId(prescription.getId());
                            return controller;
                        });
                        RootStack.push(App.getScene().getRoot());
                        App.getScene().setRoot(loader.load());
                    } catch (IOException e) {}
                }
            });
            return row;
        });
        tblView.getColumns().addAll(Arrays.asList(idCol, patientIdCol, nameCol));
        tblView.setItems(prescriptions);
    }
}
