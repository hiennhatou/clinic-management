package com.ou.clinicmanagement;

import com.ou.pojos.Medicine;
import com.ou.services.MedicineService;
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

public class MedicineManagementController implements Initializable {
    private final MedicineService medicineService = new MedicineService();

    @FXML
    public Button backBtn;
    @FXML
    public Button addMedicineBtn;
    @FXML
    public Button deleteBtn;
    @FXML
    public TableView<Medicine> tblView;
    @FXML
    public Button refreshBtn;

    ObservableList<Medicine> medicines = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(e -> App.back());
        refreshBtn.setOnAction(e -> loadMedicines());
        deleteBtn.setOnAction(e -> deleteMedicine());
        addMedicineBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = App.getFXMLLoader("modify-medicine.fxml");
                loader.setControllerFactory(p -> new AddMedicineController());
                RootStack.push(App.getScene().getRoot());
                App.getScene().setRoot(loader.load());
            } catch (IOException ex) {

            }
        });
        initTblView();
    }

    private void loadMedicines() {
        try {
            medicines.setAll(medicineService.getAllMedicines());
        } catch (SQLException e) {
        }
    }

    private void deleteMedicine() {
        Medicine selectedMedicine = tblView.getSelectionModel().getSelectedItem();

        if (selectedMedicine != null) {
            try {
                medicineService.deleteMedicineById(selectedMedicine.getId());
                medicines.remove(selectedMedicine);
            } catch (SQLException e) {

            }
        }
    }

    private void initTblView() {
        TableColumn<Medicine, Long> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Medicine, String> nameCol = new TableColumn<>("Tên thuốc");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Medicine, String> priceCol = new TableColumn<>("Giá");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Medicine, String> unitCol = new TableColumn<>("Đơn vị");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<Medicine, String> usenessCol = new TableColumn<>("Công dụng");
        usenessCol.setCellValueFactory(new PropertyValueFactory<>("useness"));
        tblView.getColumns().addAll(Arrays.asList(idCol, nameCol, priceCol, unitCol, usenessCol));
        tblView.setItems(medicines);
        tblView.setRowFactory(tv -> {
            TableRow<Medicine> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        FXMLLoader loader = App.getFXMLLoader("modify-medicine.fxml");
                        loader.setControllerFactory(p -> {
                            ModifyMedicineController controller = new ModifyMedicineController();
                            controller.setId(row.getItem().getId());
                            return controller;
                        });
                        RootStack.push(App.getScene().getRoot());
                        App.getScene().setRoot(loader.load());
                    } catch (IOException e) {

                    }
                }
            });
            return row;
        });

        loadMedicines();
    }
}
