package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileDTO {

	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;

	@NotEmpty
	private String password;

	@NotEmpty
	private String email;

	@Past
	private LocalDate birthDate;

	@NotEmpty
	private String phone;
	
	
}
