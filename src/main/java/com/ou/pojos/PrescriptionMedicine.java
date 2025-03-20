package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionMedicine {

  private long id;
  private long prescriptionId;
  private long medicineId;
  private double quantity;
  private String instrument;

}
