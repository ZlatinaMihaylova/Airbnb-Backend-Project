package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewDTO {

	@NotEmpty
	private String text;
	@Size(min = 3, max = 200,message = "Stars should be between 1 and 5")
	private int stars;
}
