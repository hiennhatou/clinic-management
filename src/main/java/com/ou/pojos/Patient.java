package com.ou.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Patient extends User {

    private LocalDate birthday;
    private String idCode;

}
