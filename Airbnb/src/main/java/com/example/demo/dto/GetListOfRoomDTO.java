package com.example.demo.dto;

import java.util.OptionalDouble;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetListOfRoomDTO {
	private String mainPhoto;
	private String name;
	private String city;
	private double rating;
	private int timesRated;
}
