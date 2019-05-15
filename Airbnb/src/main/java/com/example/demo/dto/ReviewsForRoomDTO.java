package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewsForRoomDTO {
	
	private String userName;
	private LocalDateTime date;
	private String text;
	

}
