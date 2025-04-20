package com.ou.pojos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User {
    private String idCode;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthday;
}
