package com.example.demo.dto;

import java.util.Set;

import com.example.demo.model.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

	private String name;
	private String phone;
	private Set<RoomListDTO> rooms;
	private Set<ReviewsForRoomDTO> reviews;
}
