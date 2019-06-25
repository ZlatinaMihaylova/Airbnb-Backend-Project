package com.example.demo.dto;

import java.util.List;
import java.util.Set;

import com.example.demo.model.Amenity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class AddRoomDTO {

	@NotEmpty
	private String name;

	@NotEmpty
	private String city;

	@NotEmpty
	private String address;

	@NotNull
	private int guests;

	@NotNull
	private int bedrooms;

	@NotNull
	private int beds;

	@NotNull
	private int baths;

	@NotNull
	private int price;

	@NotEmpty
	private String details;

	private List<String> amenities;


}
