package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {
  private long id;
  private long patientId;
  private String symptom;
  private String conclusion;
  private String treatmentInstruction;
  private java.sql.Timestamp examinationDate;
  private long ticketId;
}
