package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
public class AddBookingDTO {

	@Future
	private LocalDate startDate;

	@Future
	private LocalDate endDate;
}