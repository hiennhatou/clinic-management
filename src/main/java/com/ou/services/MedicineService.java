package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Ingredient;
import com.ou.pojos.Medicine;
import com.ou.pojos.MedicineIngredient;

import java.sql.*;
import java.util.*;

public class MedicineService {
    public List<Ingredient> getAllIngredients() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from ingredients");
            ResultSet rs = stm.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredients.add(ingredient);
            }
            return ingredients;
        }
    }

    public Ingredient addIngredient(Ingredient ingredient) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("insert into ingredients (name) values (?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, ingredient.getName());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                ingredient.setId(rs.getInt(1));
                return ingredient;
            }
            return null;
        }
    }

    public String updateIngredientName(long id, String newName) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("update ingredients set name = ? where id = ?");
            stm.setString(1, newName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return newName;
        }
    }

    public Medicine addMedicine(Medicine medicine) throws SQLException {
        if (medicine.getIngredients() == null || medicine.getIngredients().isEmpty())
            throw new RuntimeException("Error");
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement insertMedicineStm = conn.prepareStatement("insert into medicines(name, price, unit, useness) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertMedicineStm.setString(1, medicine.getName());
            insertMedicineStm.setDouble(2, medicine.getPrice());
            insertMedicineStm.setString(3, medicine.getUnit());
            insertMedicineStm.setString(4, medicine.getUseness());
            insertMedicineStm.executeUpdate();
            ResultSet medicineKeyResultSet = insertMedicineStm.getGeneratedKeys();
            if (medicineKeyResultSet.next()) {
                medicine.setId(medicineKeyResultSet.getLong(1));
            } else {
                conn.rollback();
                throw new RuntimeException("Error");
            }

            if (medicine.getIngredients() != null && !medicine.getIngredients().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("insert into medicine_ingredient(medicine_id, ingredient_id, quantity, unit) values ");
                stringBuilder.append(String.join(", ", new ArrayList<>(Collections.nCopies(medicine.getIngredients().size(), "(?, ?, ?, ?)"))));
                PreparedStatement insertIngredientStm = conn.prepareStatement(stringBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
                int count = 1;
                for (MedicineIngredient medicineIngredient : medicine.getIngredients()) {
                    insertIngredientStm.setLong(count++, medicine.getId());
                    insertIngredientStm.setLong(count++, medicineIngredient.getIngredient().getId());
                    insertIngredientStm.setDouble(count++, medicineIngredient.getQuantity());
                    insertIngredientStm.setString(count++, medicineIngredient.getUnit());
                }
                insertIngredientStm.executeUpdate();
                ResultSet ingredientKeyResultSet = insertIngredientStm.getGeneratedKeys();
                if (insertIngredientStm.getUpdateCount() != medicine.getIngredients().size()) {
                    conn.rollback();
                    throw new RuntimeException("Error");
                }
                for (MedicineIngredient medicineIngredient : medicine.getIngredients()) {
                    ingredientKeyResultSet.next();
                    medicineIngredient.setId(ingredientKeyResultSet.getLong(1));
                }
            }
            conn.commit();
            return medicine;
        }
    }

    public List<Medicine> getAllMedicines() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from medicines");
            ResultSet rs = stm.executeQuery();
            List<Medicine> medicines = new ArrayList<>();
            while (rs.next()) {
                Medicine medicine = new Medicine();
                medicine.setId(rs.getInt("id"));
                medicine.setName(rs.getString("name"));
                medicine.setUnit(rs.getString("unit"));
                medicine.setUseness(rs.getString("useness"));
                medicine.setPrice(rs.getInt("price"));
                medicines.add(medicine);
            }
            return medicines;
        }
    }

    public Medicine getMedicineById(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from medicines m left join medicine_ingredient mi on m.id = mi.medicine_id left join ingredients i on i.id = mi.ingredient_id where m.id = ?");
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            Medicine medicine = new Medicine();

            if (rs.next()) {
                medicine.setId(rs.getLong("m.id"));
                medicine.setName(rs.getString("m.name"));
                medicine.setUnit(rs.getString("m.unit"));
                medicine.setUseness(rs.getString("m.useness"));
                medicine.setPrice(rs.getLong("m.price"));
                do {
                    MedicineIngredient medicineIngredient = new MedicineIngredient();
                    medicineIngredient.setId(rs.getLong("mi.id"));
                    medicineIngredient.setMedicineId(medicine.getId());
                    medicineIngredient.setIngredientId(rs.getInt("mi.ingredient_id"));
                    medicineIngredient.setQuantity(rs.getDouble("mi.quantity"));
                    medicineIngredient.setUnit(rs.getString("mi.unit"));

                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("i.id"));
                    ingredient.setName(rs.getString("i.name"));
                    medicineIngredient.setIngredient(ingredient);
                    medicine.getIngredients().add(medicineIngredient);
                } while (rs.next());
            }

            return medicine;
        }
    }

    public void deleteMedicineById(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("delete from medicines where id = ?");
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    public void patchMedicine(Map<String, Object> colVals, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            String stmString = String.format("update medicines set %s where id = ?",
                String.join(", ", colVals.keySet().stream().map(v -> v + " = ?").toList()));
            PreparedStatement stm = conn.prepareStatement(stmString);
            int count = 1;
            for (Object value : colVals.values()) {
                stm.setObject(count++, value);
            }
            stm.setLong(count, id);
            stm.executeUpdate();
        }
    }

    public void deleteMedicineIngredients(Set<MedicineIngredient> mis) throws SQLException {
        if (mis.isEmpty()) return;
        try (Connection conn = DBUtils.getConnection()) {
            String stmString = String.format("delete from medicine_ingredient where id in (%s)", String.join(", ", Collections.nCopies(mis.size(), "?")));
            PreparedStatement stm = conn.prepareStatement(stmString);
            int count = 1;
            for (MedicineIngredient medicineIngredient : mis) {
                stm.setLong(count++, medicineIngredient.getId());
            }
            stm.executeUpdate();
        }
    }

    public void addMedicineIngredients(Set<MedicineIngredient> mi, long medicineId) throws SQLException {
        if (mi.isEmpty()) return;
        try (Connection conn = DBUtils.getConnection()) {
            String stmString = String.format(
                "insert into medicine_ingredient (medicine_id, ingredient_id, quantity, unit) values %s",
                String.join(", ", Collections.nCopies(mi.size(), "(?, ?, ?, ?)"))
            );
            PreparedStatement stm = conn.prepareStatement(stmString, Statement.RETURN_GENERATED_KEYS);
            int count = 1;
            for (MedicineIngredient medicineIngredient : mi) {
                stm.setLong(count++, medicineId);
                stm.setLong(count++, medicineIngredient.getIngredientId());
                stm.setDouble(count++, medicineIngredient.getQuantity());
                stm.setString(count++, medicineIngredient.getUnit());
            }
            stm.executeUpdate();
        }
    }

    public void updateMedicineIngredients(Set<MedicineIngredient> mis) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            for (MedicineIngredient medicineIngredient : mis) {
                PreparedStatement stm = conn.prepareStatement("update medicine_ingredient set quantity = ?, unit = ? where id = ?");
                stm.setDouble(1, medicineIngredient.getQuantity());
                stm.setString(2, medicineIngredient.getUnit());
                stm.setLong(3, medicineIngredient.getId());
                stm.executeUpdate();
            }
        }
    }
}
