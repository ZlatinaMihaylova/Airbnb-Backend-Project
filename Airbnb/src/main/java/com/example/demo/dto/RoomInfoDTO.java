package com.example.demo.dto;

import java.util.Set;

import com.example.demo.model.Amenity;
import com.example.demo.model.Photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomInfoDTO {
	private String address;
	private int guests;
	private int bedrooms;
	private int beds;
	private int baths;
	private int price;
	private String details;
	private Set<String> photos;
	private Set<String> amenities;
}
