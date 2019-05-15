package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WriteReviewDTO {

	private String text;
	private int stars;
}
