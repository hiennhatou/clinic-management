package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
  private LocalDateTime examinationDate;
  private long ticketId;
}
