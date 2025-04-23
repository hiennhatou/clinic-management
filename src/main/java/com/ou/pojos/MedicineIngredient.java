package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicineIngredient {
  private long id;
  private long medicineId;
  private long ingredientId;
  private double quantity;
  private String unit;
  private Ingredient ingredient;
}
