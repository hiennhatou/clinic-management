package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {

  private long id;
  private long medicalRecordId;
  private long isProvided;
  private List<PrescriptionMedicine> medicines;

}
