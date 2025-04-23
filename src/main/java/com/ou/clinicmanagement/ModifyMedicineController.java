package com.ou.clinicmanagement;

import com.ou.pojos.Medicine;
import com.ou.pojos.MedicineIngredient;
import com.ou.utils.exceptions.ValidatorException;
import com.ou.utils.validation.MedicineIngredientValidation;
import com.ou.utils.validation.MedicineValidation;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import lombok.Setter;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ModifyMedicineController extends MedicineController {
    @Setter
    private long id;
    private Medicine medicine;
    private final Set<MedicineIngredient> deletedIngredients = new HashSet<>();
    private final Set<MedicineIngredient> addedIngredients = new HashSet<>();
    private final Set<MedicineIngredient> updatedIngredients = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        loadMedicine();
        saveBtn.setOnAction(event -> onUpdate());
    }

    @Override
    protected MedicineIngredient onDeleteIngredient() {
        MedicineIngredient mi = super.onDeleteIngredient();
        if (mi.getId() > 0)
            deletedIngredients.add(mi);
        return mi;
    }

    @Override
    protected MedicineIngredient addIngredientToTableView() {
        MedicineIngredient mi = super.addIngredientToTableView();
        addedIngredients.add(mi);
        return mi;
    }

    @Override
    protected void initIngredientTblView() {
        super.initIngredientTblView();
        EventHandler<TableColumn.CellEditEvent<MedicineIngredient, Double>> quantityColOnEditCommit = quantityCol.getOnEditCommit();
        quantityCol.setOnEditCommit(event -> {
            if (event.getRowValue().getId() > 0 && !event.getNewValue().equals(event.getOldValue()))
                updatedIngredients.add(event.getRowValue());
            quantityColOnEditCommit.handle(event);
        });

        EventHandler<TableColumn.CellEditEvent<MedicineIngredient, String>> unitColOnEditCommit = unitCol.getOnEditCommit();
        unitCol.setOnEditCommit(event -> {
            if (event.getRowValue().getId() > 0 && !event.getNewValue().equals(event.getOldValue()))
                updatedIngredients.add(event.getRowValue());
            unitColOnEditCommit.handle(event);
        });
    }

    private void loadMedicine() {
        try {
            medicine = medicineService.getMedicineById(id);
            ingredientTblView.getItems().addAll(medicine.getIngredients());
            nameTF.setText(medicine.getName());
            priceTF.setText(String.valueOf(medicine.getPrice()));
            unitTF.setText(medicine.getUnit());
            uselessTF.setText(medicine.getUseness());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onUpdate() {
        String medicineName = nameTF.getText();
        String medicinePrice = priceTF.getText();
        String medicineUnit = unitTF.getText();
        String medicineUseness = uselessTF.getText();

        try {
            medicineName = MedicineValidation.validateMedicineName(medicineName);
            long medicinePriceInLong = MedicineValidation.validateMedicinePrice(medicinePrice);
            medicineUnit = MedicineValidation.validateMedicineUnit(medicineUnit);
            medicineUseness = MedicineValidation.validateMedicineUseness(medicineUseness);

            HashMap<String, Object> map = new HashMap<>();
            if (!medicineName.equals(medicine.getName().trim())) {
                map.put("name", medicineName);
            }
            if (medicinePriceInLong != medicine.getPrice()) {
                map.put("price", medicinePriceInLong);
            }
            if (!medicineUnit.equals(medicine.getUnit().trim())) {
                map.put("unit", medicineUnit);
            }
            if (!medicineUseness.equals(medicine.getUseness().trim())) {
                map.put("useness", medicineUseness);
            }

            if (!map.isEmpty()) {
                medicineService.patchMedicine(map, medicine.getId());
            }

            if (!deletedIngredients.isEmpty()) {
                medicineService.deleteMedicineIngredients(deletedIngredients);
            }

            if (!addedIngredients.isEmpty()) {
                for (MedicineIngredient medicineIngredient : addedIngredients) {
                    MedicineIngredientValidation.validate(medicineIngredient);
                }
                medicineService.addMedicineIngredients(addedIngredients, medicine.getId());
            }

            if (!updatedIngredients.isEmpty()) {
                for (MedicineIngredient medicineIngredient : updatedIngredients) {
                    MedicineIngredientValidation.validate(medicineIngredient);
                }
                medicineService.updateMedicineIngredients(updatedIngredients);
            }

            App.back();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ValidatorException e) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", e.getMessage(), null, null);
        }
    }
}
