package com.ou.clinicmanagement;

import com.ou.pojos.*;
import com.ou.services.*;
import com.ou.utils.exceptions.ValidatorException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lombok.Setter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TicketDetailController implements Initializable {
    @FXML
    public Button backBtn;
    @FXML
    public Button commitStatusBtn;
    @FXML
    public Text nameTx;
    @FXML
    public Text birthTx;
    @FXML
    public Text createdOnTx;
    @FXML
    public Text statusTx;
    @FXML
    public Tab allergicTab;
    @FXML
    public SearchableComboBox<Ingredient> ingredientCB;
    @FXML
    public Button addIngredientToAllergicTblViewBtn;
    @FXML
    public Button deleteAllergicIngredientBtn;
    @FXML
    public ListView<AllergicIngredient> allergicIngredientLV;
    @FXML
    public Tab medicalRecordHistoryTab;
    @FXML
    public TableView<MedicalRecord> medicalRecordHistoryTblView;
    @FXML
    public Tab medicalRecordTab;
    @FXML
    public TextArea symptomTF;
    @FXML
    public TextArea conclusionTF;
    @FXML
    public TextArea instrumentTF;
    @FXML
    public Button saveMedicalRecordBtn;
    @FXML
    public Tab prescriptionTab;
    @FXML
    public TableView<PrescriptionMedicine> prescriptionTblView;
    @FXML
    public SearchableComboBox<Medicine> medicineCB;
    @FXML
    public TextField quantityTF;
    @FXML
    public TextField medicineInstrumentTF;
    @FXML
    public Button addMedicineToPrescriptionBtn;
    @FXML
    public Button deleteMedicineBtn;
    @FXML
    public Button addPrescriptionBtn;
    @FXML
    public Button requirePrescriptionBtn;

    MedicineService medicineService = new MedicineService();
    TicketService ticketService = new TicketService();
    AllergicService allergicService = new AllergicService();
    MedicalRecordService medicalRecordService = new MedicalRecordService();
    PrescriptionService prescriptionService = new PrescriptionService();
    ObservableList<PrescriptionMedicine> prescriptionMedicines = FXCollections.observableArrayList();

    @Setter
    private long id;
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
    private final ObservableList<AllergicIngredient> allergicIngredients = FXCollections.observableArrayList();
    private final SimpleObjectProperty<Prescription> prescription = new SimpleObjectProperty<>();
    private Ticket ticket;
    private MedicalRecord medicalRecord;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(event -> App.back());
        commitStatusBtn.setOnAction(event -> onCommit());
        status.addListener((observable, oldValue, newValue) -> {
            boolean isDisable = !newValue.equals("checked_in");
            if (newValue.equals("done")) {
                App.showAlert(Alert.AlertType.INFORMATION, "Lưu ý", "Phiên khám đã kết thúc", null, (e) -> App.back());
                return;
            }
            medicalRecordHistoryTab.setDisable(isDisable);
            medicalRecordTab.setDisable(isDisable);
            prescriptionTab.setDisable(isDisable);
            allergicTab.setDisable(isDisable);
            if (newValue.equals("checked_in")) {
                initAllergicTab();
                initMedicalRecordTab();
                initPrescriptionTab();
            }
            if (newValue.equals("created")) {
                commitStatusBtn.setText("Tiếp nhận");
            } else {
                commitStatusBtn.setText("Đã hoàn thành");
            }
        });
        prescription.addListener((observable, oldValue, newValue) -> {
            boolean isDisable = newValue == null || !newValue.getStatus().equals("created");
            medicineCB.setDisable(isDisable);
            quantityTF.setDisable(isDisable);
            medicineInstrumentTF.setDisable(isDisable);
            addMedicineToPrescriptionBtn.setDisable(isDisable);
            deleteMedicineBtn.setDisable(isDisable);
            requirePrescriptionBtn.setDisable(isDisable);
        });

        loadTicket();
        initTicketInform();
    }

    /// General Init
    private void initTicketInform() {
        nameTx.setText(ticket.getPatient().getLastName() + " " + ticket.getPatient().getMiddleName() + " " + ticket.getPatient().getFirstName());
        birthTx.setText(ticket.getPatient().getBirthday().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        createdOnTx.setText(ticket.getCreatedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        statusTx.setText(Ticket.getReadableStatus(ticket.getStatus()));
    }

    private void onCommit() {
        try {
            if (status.get().equals("created")) {
                ticket.setStatus(ticketService.updateStatus(id, ticket.getPatientId(), "checked_in"));
            } else if (status.get().equals("checked_in")) {
                ticket.setStatus(ticketService.updateStatus(id, ticket.getPatientId(), "done"));
            }
            status.set(ticket.getStatus());
        } catch (SQLException e) {
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage(), null, null);
            App.back();
        }
    }

    private void loadTicket() {
        try {
            ticket = ticketService.getTicketById(id);
            if (ticket == null) throw new RuntimeException();
            status.set(ticket.getStatus());
        } catch (SQLException | RuntimeException e) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Hệ thống gặp lỗi", null, null);
            App.back();
        }
    }

    /// Init allergic ingredient
    private void initAllergicTab() {
        initIngredientComboBox();
        initAllergicTblView();
        addIngredientToAllergicTblViewBtn.setOnAction(event -> onAddAllergic());
        deleteAllergicIngredientBtn.setOnAction(event -> onDeleteAllergic());

        try {
            ingredients.setAll(medicineService.getAllIngredients());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allergicIngredients.setAll(allergicService.getAllergicIngredientByPatient(ticket.getPatientId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initIngredientComboBox() {
        ingredientCB.setCellFactory((lv) -> new ListCell<>() {
            @Override
            protected void updateItem(Ingredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %d", item.getName(), item.getId()));
                }
            }
        });

        ingredientCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Ingredient object) {
                return object != null ? String.format("%s - %d", object.getName(), object.getId()) : null;
            }

            @Override
            public Ingredient fromString(String string) {
                return null;
            }
        });

        ingredientCB.setItems(ingredients);
    }

    private void initAllergicTblView() {
        allergicIngredientLV.setCellFactory((lv) -> new ListCell<>() {
            @Override
            protected void updateItem(AllergicIngredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIngredient().getName());
                }
            }
        });
        allergicIngredientLV.setItems(allergicIngredients);
    }

    private void onAddAllergic() {
        Ingredient ingredient = ingredientCB.getSelectionModel().getSelectedItem();
        if (ingredient == null) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn thành phân để lưu", null, null);
            return;
        }

        if (allergicIngredients.stream().anyMatch(a -> a.getIngredientId() == ingredient.getId())) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Thành phần đã tồn tại trong danh sách dị ứng", null, null);
            return;
        }

        try {
            AllergicIngredient ai = allergicService.addAllergicIngredient(ingredient, ticket.getPatientId());
            allergicIngredients.add(ai);
            ingredientCB.setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDeleteAllergic() {
        AllergicIngredient allergicIngredient = allergicIngredientLV.getSelectionModel().getSelectedItem();
        if (allergicIngredient == null) {
            App.showAlert(Alert.AlertType.ERROR, "Lưu ý", "Chưa chọn thành phần dị ứng để xóa", null, null);
            return;
        }
        try {
            allergicService.deleteAllergicIngredient(allergicIngredient.getId());
            allergicIngredients.remove(allergicIngredient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Medical Record
    private void initMedicalRecordTab() {
        saveMedicalRecordBtn.setOnAction(event -> onSaveMedicalRecord());
        try {
            medicalRecord = medicalRecordService.getMedicalRecordByTicketId(id);
            if (medicalRecord != null) {
                symptomTF.setText(medicalRecord.getSymptom());
                conclusionTF.setText(medicalRecord.getConclusion());
                instrumentTF.setText(medicalRecord.getTreatmentInstruction());
            }
        } catch (Exception e) {
        }
    }

    private void onSaveMedicalRecord() {
        String symptom = symptomTF.getText();
        String conclusion = conclusionTF.getText();
        String instrument = instrumentTF.getText();
        if (symptom == null || symptom.isBlank() || conclusion == null || conclusion.isBlank()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Vui lòng nhập đầy đủ thông tin", null, null);
            return;
        }

        try {
            MedicalRecord newMedicalRecord = new MedicalRecord();
            newMedicalRecord.setSymptom(symptom);
            newMedicalRecord.setConclusion(conclusion);
            newMedicalRecord.setTreatmentInstruction(instrument);
            newMedicalRecord.setTicketId(id);
            newMedicalRecord.setPatientId(ticket.getPatientId());
            medicalRecord = medicalRecordService.addMedicalRecord(newMedicalRecord);
            App.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Lưu thông tin thành công", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Prescription
    private void initPrescriptionTab() {
        try {
            prescription.set(prescriptionService.getPrescriptionByTicketId(id));
        } catch (SQLException e) {
            prescription.set(null);
            e.printStackTrace();
        }
    }

    private void onCreatePrescription() {
        if (prescription.get() == null)
            try {
                prescription.set(prescriptionService.insertPrescription(id));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void onAddPrescriptionMedicine() {
        if (prescription.get() == null) return;

        Medicine medicine = medicineCB.getValue();
        String quantity = quantityTF.getText();
        String instrument = medicineInstrumentTF.getText();

        if (medicine == null) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Vui lòng chọn thuốc để thêm", null, null);
            return;
        }

        if (quantity == null || quantity.isBlank()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Vui lòng nhập số lượng thuốc", null, null);
            return;
        }

        if (instrument == null || instrument.isBlank()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Vui lòng nhập hướng dẫn với thuốc", null, null);
            return;
        }

        if (prescriptionMedicines.stream().anyMatch(pm -> pm.getMedicineId() == medicine.getId())) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Thuốc đã tồn tại trong đơn thuốc", null, null);
            return;
        }

        try {
            double quantityDouble = Double.parseDouble(quantity);
            PrescriptionMedicine pm = new PrescriptionMedicine();
            pm.setMedicineId(medicine.getId());
            pm.setPrescriptionId(prescription.get().getId());
            pm.setQuantity(quantityDouble);
            prescriptionService.addPrescriptionMedicine(pm);
            prescriptionMedicines.add(pm);
            medicineCB.setValue(null);
            quantityTF.setText("");
            instrumentTF.setText("");
        } catch (NumberFormatException e) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Số lượng không hợp lệ", null, null);
        } catch (SQLException e) {}
    }

    private void onDeletePrescriptionMedicine() {
        if (prescription.get() == null) return;
        PrescriptionMedicine pm = prescriptionTblView.getSelectionModel().getSelectedItem();
        if (pm == null) return;

        try {
            prescriptionService.removePrescriptionMedicine(pm.getId());
            prescriptionMedicines.remove(pm);
        } catch (SQLException e) {}
    }

    private void onRequirePrescription() {
        if (prescription.get() == null || !prescription.get().getStatus().equals("created")) return;
        try {
            prescriptionService.updatePrescriptionStatus("required", prescription.get().getId());
            prescription.get().setStatus("required");
            prescription.set(prescription.get());
        } catch (SQLException e) {

        }
    }
}
