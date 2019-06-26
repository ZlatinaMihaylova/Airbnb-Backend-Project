package com.example.demo.dto;

import java.util.List;
import java.util.Set;

import com.example.demo.model.Amenity;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddRoomDTO {

	@NotEmpty
	private String name;

	@NotEmpty
	private String city;

	@NotEmpty
	private String address;

	@Positive
	private int guests;

	@Positive
	private int bedrooms;

	@Positive
	private int beds;

	@Positive
	private int baths;

	@Positive
	private int price;

	@NotEmpty
	private String details;

	private List<String> amenities;


}
