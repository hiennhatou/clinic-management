package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ticket {
  private long id;
  private long patientId;
  private long doctorId;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  private String status;

  private Patient patient;
  private User doctor;
  private MedicalRecord medicalRecord;

  public static String getReadableStatus(String status) {
    return switch (status) {
      case "created" -> "Vừa được tạo";
      case "checked_in" -> "Đã tiếp nhận";
      case "done" -> "Đã hoàn thành";
      default -> null;
    };
  }
}
