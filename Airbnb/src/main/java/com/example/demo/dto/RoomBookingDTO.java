package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomBookingDTO {
	private LocalDate startDate;
	private LocalDate endDate;
	private long roomId;
}
