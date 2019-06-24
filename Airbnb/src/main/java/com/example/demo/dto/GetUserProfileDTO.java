package com.example.demo.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"name","phone","rooms","reviews"})
public class GetUserProfileDTO {

	private String name;
	private String phone;
	private List<GetListOfRoomDTO> rooms;
	private List<GetReviewsForRoomDTO> reviews;

}
