package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Medicine {
  private long id;
  private String name;
  private long price = -1;
  private String unit;
  private String useness;
  private List<MedicineIngredient> ingredients = new ArrayList<>();
}
