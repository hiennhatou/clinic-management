package com.ou.services;

import com.ou.pojos.AllergicIngredient;
import com.ou.pojos.DBUtils;
import com.ou.pojos.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllergicService {
    public List<AllergicIngredient> getAllergicIngredientByPatient(long patientId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from allergic_ingredients a left join ingredients i on i.id = a.ingredient_id where patient_id = ?");
            preparedStatement.setLong(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<AllergicIngredient> allergicIngredients = new ArrayList<>();
            while (resultSet.next()) {
                AllergicIngredient allergicIngredient = new AllergicIngredient();
                allergicIngredient.setId(resultSet.getInt("a.id"));
                allergicIngredient.setPatientId(resultSet.getInt("a.patient_id"));
                allergicIngredient.setIngredientId(resultSet.getInt("a.ingredient_id"));

                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("i.id"));
                ingredient.setName(resultSet.getString("i.name"));
                allergicIngredient.setIngredient(ingredient);
                allergicIngredients.add(allergicIngredient);
            }
            return allergicIngredients;
        }
    }

    public AllergicIngredient addAllergicIngredient(Ingredient ingredient, long patientId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("insert into allergic_ingredients (patient_id, ingredient_id) values(?, ?)", Statement.RETURN_GENERATED_KEYS);
            stm.setLong(1, patientId);
            stm.setLong(2, ingredient.getId());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                AllergicIngredient allergicIngredient = new AllergicIngredient();
                allergicIngredient.setId(rs.getLong(1));
                allergicIngredient.setPatientId(patientId);
                allergicIngredient.setIngredientId(ingredient.getId());
                allergicIngredient.setIngredient(ingredient);
                return allergicIngredient;
            }
            return null;
        }
    }

    public boolean deleteAllergicIngredient(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("delete from allergic_ingredients where id = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            return true;
        }
    }
}
