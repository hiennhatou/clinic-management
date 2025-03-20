package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointments {

  private long id;
  private long patientId;
  private String periodTime;
  private java.sql.Date appointmentDate;
  private String symptom;
  private long isMeet;

}
