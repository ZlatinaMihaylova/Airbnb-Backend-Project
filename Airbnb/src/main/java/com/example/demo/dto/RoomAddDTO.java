package com.example.demo.dto;

import java.util.Set;

import com.example.demo.model.Amenity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomAddDTO {
	private String name;
	private String address;
	private int guests;
	private int bedrooms;
	private int beds;
	private int baths;
	private int price;
	private String details;
	private Set<String> amenities;
	private String city;
}
