package com.example.demo.dto;

import java.util.List;
import java.util.Set;

import com.example.demo.model.Room;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"name","phone","rooms","reviews"})
public class UserProfileDTO {

	private String name;
	private String phone;
	private List<RoomListDTO> rooms;
	private List<ReviewsForRoomDTO> reviews;

}
