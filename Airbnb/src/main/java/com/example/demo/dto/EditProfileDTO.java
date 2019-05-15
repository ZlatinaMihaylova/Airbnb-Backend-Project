package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class EditProfileDTO {

	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private LocalDate birthDate;
	private String phone;
	
	
}
