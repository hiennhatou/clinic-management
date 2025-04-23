package com.ou.clinicmanagement;

import com.ou.pojos.Ingredient;
import com.ou.pojos.MedicineIngredient;
import com.ou.services.MedicineService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MedicineController implements Initializable {
    protected final MedicineService medicineService = new MedicineService();
    protected final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();

    @FXML
    public Button deleteBtn;
    @FXML
    public Button saveBtn;
    @FXML
    public TextField nameTF;
    @FXML
    public TextField priceTF;
    @FXML
    public TextField unitTF;
    @FXML
    public TextField uselessTF;
    @FXML
    public SearchableComboBox<Ingredient> ingredientComboBox;
    @FXML
    public Button addIngredientBtn;
    @FXML
    public Button backBtn;
    @FXML
    public TableView<MedicineIngredient> ingredientTblView;
    @FXML
    public TextField addIngredientTF;
    @FXML
    public Button insertIngredientBtn;
    @FXML
    public Button deleteIngredientBtn;

    protected TableColumn<MedicineIngredient, String> nameCol = new TableColumn<>("Tên");
    protected TableColumn<MedicineIngredient, Double> quantityCol = new TableColumn<>("Lượng");
    protected TableColumn<MedicineIngredient, String> unitCol = new TableColumn<>("Đơn vị");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(event -> App.back());
        insertIngredientBtn.setOnAction(event -> addNewIngredient());
        addIngredientBtn.setOnAction(event -> addIngredientToTableView());
        deleteIngredientBtn.setOnAction(event -> onDeleteIngredient());
        initIngredientComboBox();
        initIngredientTblView();
    }

    protected void initIngredientComboBox() {
        ingredientComboBox.setCellFactory((lv) -> new ListCell<>() {
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

        ingredientComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Ingredient object) {
                return String.format("%s - %d", object.getName(), object.getId());
            }

            @Override
            public Ingredient fromString(String string) {
                return null;
            }
        });

        ingredientComboBox.setItems(ingredients);

        try {
            ingredients.addAll(medicineService.getAllIngredients());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initIngredientTblView() {
        nameCol.setCellValueFactory(t -> new SimpleStringProperty(t.getValue().getIngredient().getName()));

        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                return String.valueOf((double) object);
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        }));
        quantityCol.setOnEditCommit(event -> {
            if (event.getNewValue() != null) {
                event.getRowValue().setQuantity(event.getNewValue());
            } else {
                ingredientTblView.refresh();
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Lượng thành phần không hợp lệ", null, null);
            }
        });

        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitCol.setCellFactory(TextFieldTableCell.forTableColumn());
        unitCol.setOnEditCommit(event -> {
            if (event.getNewValue() != null && !event.getNewValue().isBlank()) {
                event.getRowValue().setUnit(event.getNewValue().trim());
            } else {
                ingredientTblView.refresh();
                App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Đơn vị không hợp lệ", null, null);
            }
        });

        ingredientTblView.getColumns().addAll(Arrays.asList(nameCol, quantityCol, unitCol));
    }

    protected void addNewIngredient() {
        String ingredientName = addIngredientTF.getText();
        if (ingredientName == null || ingredientName.isBlank()) return;
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientName);
        try {
            medicineService.addIngredient(ingredient);
            ingredients.add(ingredient);
            addIngredientTF.clear();
        } catch (SQLException e) {
        }
    }

    protected MedicineIngredient addIngredientToTableView() {
        Ingredient ingredient = ingredientComboBox.getValue();

        if (ingredient == null) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Vui lòng chọn thành phần để thêm vào thuốc", null, null);
            return null;
        }

        if (ingredientTblView.getItems().stream().anyMatch(mi -> mi.getIngredient().equals(ingredientComboBox.getValue()))) {
            App.showAlert(Alert.AlertType.ERROR, "Thông tin chưa được lưu", "Thành phần đã tồn tại trong thuốc", null, null);
            return null;
        }

        MedicineIngredient medicineIngredient = new MedicineIngredient();
        medicineIngredient.setIngredient(ingredient);
        medicineIngredient.setIngredientId(ingredient.getId());
        ingredientTblView.getItems().add(medicineIngredient);
        ingredientComboBox.setValue(null);
        return medicineIngredient;
    }

    protected MedicineIngredient onDeleteIngredient() {
        MedicineIngredient mi = ingredientTblView.getSelectionModel().getSelectedItem();
        if (mi != null)
            ingredientTblView.getItems().remove(mi);
        return null;
    }
}
