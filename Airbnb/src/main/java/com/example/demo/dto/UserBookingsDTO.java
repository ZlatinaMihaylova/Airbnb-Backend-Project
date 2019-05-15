package com.example.demo.dto;


import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserBookingsDTO {
	private Long roomId;
	private LocalDate startDate;
	private LocalDate endDate;
}
