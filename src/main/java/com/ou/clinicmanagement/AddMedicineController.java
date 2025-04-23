package com.ou.clinicmanagement;

import com.ou.pojos.Medicine;
import com.ou.services.MedicineService;
import javafx.scene.control.Alert;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddMedicineController extends MedicineController {
    private final MedicineService medicineService = new MedicineService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        saveBtn.setOnAction(event -> onSaveMedicine());
        deleteBtn.setVisible(false);
    }

    private void onSaveMedicine() {
        if (nameTF.getText() == null || nameTF.getText().isBlank()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Tên thuốc không hợp lệ", null, null);
            return;
        }

        if (priceTF.getText() == null) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Giá thuốc không được trônống", null, null);
            return;
        }

        if (ingredientComboBox.getItems().isEmpty()) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Thuốc phải chứa ít nhất 1 thành phần", null, null);
            return;
        }

        if (ingredientTblView.getItems().stream().anyMatch(t -> t.getQuantity() <= 0.0 || t.getUnit() == null || t.getUnit().isBlank())) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Các thành phần phải có lượng lớn hơn 0 và có đơn vị rõ ràng", null, null);
            return;
        }

        Medicine medicine = new Medicine();
        medicine.setName(nameTF.getText());
        medicine.setUnit(unitTF.getText());
        medicine.setIngredients(ingredientTblView.getItems());
        medicine.setUseness(uselessTF.getText());
        try {
            medicine.setPrice(Long.parseLong(priceTF.getText()));
        } catch (NumberFormatException e) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Giá thuốc không hợp lệ", null, null);
            return;
        }

        if (medicine.getPrice() <= 0) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Giá thuốc phải lớn hơn 0", null, null);
            return;
        }

        try {
            medicineService.addMedicine(medicine);
            App.back();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
