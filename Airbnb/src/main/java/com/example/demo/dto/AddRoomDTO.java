package com.example.demo.dto;

import java.util.Set;

import com.example.demo.model.Amenity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
public class AddRoomDTO {

	@NotEmpty
	private String name;

	@NotEmpty
	private String address;

	@NotEmpty
	private int guests;

	@NotEmpty
	private int bedrooms;

	@NotEmpty
	private int beds;

	@NotEmpty
	private int baths;

	@NotEmpty
	private int price;

	@NotEmpty
	private String details;


	private Set<String> amenities;

	@NotEmpty
	private String city;
}
