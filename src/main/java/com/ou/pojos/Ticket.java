package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ticket {
  private long id;
  private long patientId;
  private java.sql.Timestamp createdOn;
  private java.sql.Timestamp updatedOn;
}
