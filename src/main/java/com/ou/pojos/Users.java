package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {

  private long id;
  private String firstName;
  private String lastName;
  private String middleName;
  private String username;
  private String role;
  private String password;

}
