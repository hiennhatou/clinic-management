package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
  private long id;
  private long patientId;
  private LocalDate appointmentDate;
  private boolean isCheckin = false;

  private Patient patient;
}
