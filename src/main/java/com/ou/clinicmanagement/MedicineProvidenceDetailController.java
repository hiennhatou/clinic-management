package com.ou.clinicmanagement;

import com.ou.pojos.Medicine;
import com.ou.pojos.Prescription;
import com.ou.pojos.PrescriptionMedicine;
import com.ou.services.MedicineService;
import com.ou.services.PrescriptionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lombok.Setter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class MedicineProvidenceDetailController implements Initializable {
    @FXML
    public SearchableComboBox<Medicine> medicineCB;
    @FXML
    public TextField quantityTF;
    @FXML
    public TextField instrumentTF;
    @FXML
    public Button addMedicineBtn;
    @FXML
    public Button provideMedicineBtn;
    @FXML
    public Button deleteBtn;
    @FXML
    public TableView<PrescriptionMedicine> prescriptionMedicineTblView;
    @FXML
    public Button backBtn;
    @FXML
    public Text priceTX;

    @Setter
    private long id;

    private final MedicineService medicineService = new MedicineService();
    private final PrescriptionService prescriptionService = new PrescriptionService();

    private final ObservableList<PrescriptionMedicine> prescriptionMedicines = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private Prescription prescription;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(event -> App.back());
        addMedicineBtn.setOnAction(event -> onAddMedicine());
        deleteBtn.setOnAction(event -> onDeletePrescriptionMedicine());
        provideMedicineBtn.setOnAction(event -> onProvidePrescription());
        prescriptionMedicines.addListener((ListChangeListener<PrescriptionMedicine>) c -> {
            Optional<Double> totalPrice = c.getList().stream().map(i -> i.getMedicine().getPrice() * i.getQuantity()).reduce(Double::sum);
            totalPrice.ifPresent(aDouble -> priceTX.setText("Giá: " + String.format("%.2f", aDouble)));
        });
        loadPrescription();
        initMedicineCB();
        initPrescriptionMedicineTblView();
    }

    private void initAction() {
        boolean condition = prescription != null && prescription.getStatus().equals("required");
        quantityTF.setDisable(!condition);
        instrumentTF.setDisable(!condition);
        addMedicineBtn.setDisable(!condition);
        deleteBtn.setDisable(!condition);
        provideMedicineBtn.setDisable(!condition);
        medicineCB.setDisable(!condition);
        prescriptionMedicineTblView.setDisable(!condition);
    }

    private void initMedicineCB() {
        medicineCB.setItems(medicines);
        try {
            medicines.addAll(medicineService.getAllMedicines());
        } catch (SQLException e) {
        }
        medicineCB.setCellFactory(t -> new ListCell<>() {
            @Override
            protected void updateItem(Medicine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        medicineCB.setConverter(new StringConverter<Medicine>() {
            @Override
            public String toString(Medicine object) {
                return object != null ? object.getName() : null;
            }

            @Override
            public Medicine fromString(String string) {
                return null;
            }
        });
    }

    private void initPrescriptionMedicineTblView() {
        TableColumn<PrescriptionMedicine, Medicine> nameCol = new TableColumn<>("Tên thuốc");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("medicine"));
        nameCol.setCellFactory(t -> new TableCell<>() {
            @Override
            protected void updateItem(Medicine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        TableColumn<PrescriptionMedicine, Double> quantityCol = new TableColumn<>("Số lượng");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(t -> {
            TextFieldTableCell<PrescriptionMedicine, Double> tableCell = new TextFieldTableCell<>();
            tableCell.setConverter(new StringConverter<>() {
                @Override
                public String toString(Double object) {
                    return String.valueOf(object);
                }

                @Override
                public Double fromString(String string) {
                    try {
                        return Double.parseDouble(string);
                    } catch (NumberFormatException e) {
                        return 0D;
                    }
                }
            });
            return tableCell;
        });
        quantityCol.setOnEditCommit(event -> {
            if (event.getNewValue() != null && event.getNewValue() > 0){
                try {
                    prescriptionService.updateQuantityPrescriptionMedicine(event.getRowValue().getId(), event.getNewValue());
                    event.getRowValue().setQuantity(event.getNewValue());
                    calculatePrice();
                } catch (SQLException e) {
                    App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa lưu", "Hệ thống gặp lỗi", null, null);
                    prescriptionMedicineTblView.refresh();
                }
            } else {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa lưu", "Số lượng thuốc phải lớn hơn 0", null, null);
                prescriptionMedicineTblView.refresh();
            }
        });

        TableColumn<PrescriptionMedicine, String> instrumentCol = new TableColumn<>("Chỉ định");
        instrumentCol.setCellValueFactory(new PropertyValueFactory<>("instrument"));
        instrumentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        instrumentCol.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            if (newValue == null || newValue.isBlank()) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa lưu", "Chỉ định của thuốc không được trống", null, null);
                prescriptionMedicineTblView.refresh();
                return;
            }

            try {
                prescriptionService.updateInstrumentPrescriptionMedicine(event.getRowValue().getId(), newValue);
                event.getRowValue().setInstrument(newValue);
            } catch (SQLException e) {
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa lưu", "Hệ thống gặp lỗi", null, null);
                prescriptionMedicineTblView.refresh();
            }
        });
        TableColumn<PrescriptionMedicine, String> pricePerUnitCol = new TableColumn<>("Giá trên 1 đơn vị");
        pricePerUnitCol.setCellValueFactory(t -> new SimpleStringProperty(String.valueOf(t.getValue().getMedicine().getPrice())));

        prescriptionMedicineTblView.getColumns().addAll(Arrays.asList(nameCol, quantityCol, instrumentCol, pricePerUnitCol));
        prescriptionMedicineTblView.setItems(prescriptionMedicines);
    }

    private void loadPrescription() {
        try {
            prescription = prescriptionService.getPrescriptionById(id);
            if (prescription != null && !prescription.getMedicines().isEmpty())
                prescriptionMedicines.setAll(prescription.getMedicines());
            if (prescription == null || !prescription.getStatus().equals("required")) {
                App.showAlert(Alert.AlertType.ERROR, "Thông báo", "Đơn thuốc không hợp lệ", null, event -> App.back());
            }
            initAction();
        } catch (SQLException e) {
            App.back();
            App.showAlert(Alert.AlertType.ERROR, "Thông báo", "Đơn thuốc không hợp lệ", null, null);
        }
    }

    private void onAddMedicine() {
        Medicine medicine = medicineCB.getSelectionModel().getSelectedItem();
        String quantity = quantityTF.getText();
        String instrument = instrumentTF.getText();

        if (medicine == null) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Vui lòng chọn thuốc để thêm", null, null);
            return;
        }

        if (quantity == null || quantity.isEmpty()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Số lượng thuốc không được trống", null, null);
            return;
        }

        if (instrument == null || instrument.isEmpty()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Chỉ định với thuốc không được trống", null, null);
            return;
        }

        try {
            double quantityDouble = Double.parseDouble(quantity);
            PrescriptionMedicine prescriptionMedicine = new PrescriptionMedicine();
            prescriptionMedicine.setMedicine(medicine);
            prescriptionMedicine.setQuantity(quantityDouble);
            prescriptionMedicine.setInstrument(instrument);
            prescriptionMedicine.setMedicineId(medicine.getId());
            prescriptionMedicine.setPrescriptionId(prescription.getId());
            prescriptionService.addPrescriptionMedicine(prescriptionMedicine);
            prescriptionMedicines.add(prescriptionMedicine);
            medicineCB.setValue(null);
            quantityTF.clear();
            instrumentTF.clear();
        } catch (NumberFormatException | SQLException e) {}
    }

    private void onDeletePrescriptionMedicine() {
        PrescriptionMedicine prescriptionMedicine = prescriptionMedicineTblView.getSelectionModel().getSelectedItem();
        if (prescriptionMedicine == null) return;
        try {
            prescriptionService.removePrescriptionMedicine(prescriptionMedicine.getId());
            prescriptionMedicines.remove(prescriptionMedicine);
        } catch (SQLException e) {

        }
    }

    private void onProvidePrescription() {
        if (!prescription.getStatus().equals("required")) return;
        try {
            prescriptionService.updatePrescriptionStatus("provided", prescription.getId());
            prescription.setStatus("provided");
            initAction();
        } catch (SQLException e) {

        }
    }

    private void calculatePrice() {
        Optional<Double> totalPrice = prescriptionMedicines.stream().map(i -> i.getMedicine().getPrice() * i.getQuantity()).reduce(Double::sum);
        totalPrice.ifPresent(aDouble -> priceTX.setText("Giá: " + String.format("%.2f", aDouble)));
    }
}
