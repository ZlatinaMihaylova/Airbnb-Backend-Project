package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomBookingDTO {
	private long roomId;
	private LocalDate startDate;
	private LocalDate endDate;
}
