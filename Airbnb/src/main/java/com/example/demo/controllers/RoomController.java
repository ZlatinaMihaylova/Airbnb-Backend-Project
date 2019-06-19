package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;

import com.example.demo.model.User;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.service.RoomService;;

@RestController
public class RoomController {
	
	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;

	@GetMapping("/rooms/roomId={roomId}")
	public RoomInfoDTO getRoomById(@PathVariable long roomId) throws ElementNotFoundException {
		return roomService.convertRoomToRoomInfoDTO(roomService.getRoomById(roomId));
	}

	@GetMapping("/rooms")
	public List<RoomListDTO> getAllRooms(HttpServletResponse response) throws ElementNotFoundException{
		return roomService.getAllRooms().stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@PostMapping("/rooms/create")
	public RoomInfoDTO addRoom(@RequestBody RoomAddDTO newRoom,HttpServletRequest request) throws ElementNotFoundException, UnauthorizedException {
		long id = UserService.authentication(request);
		return roomService.convertRoomToRoomInfoDTO(roomService.addRoom(newRoom,id));
	}

	@PostMapping("/rooms/delete/{roomId}")
	public void removeRoom(@PathVariable long roomId,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		roomService.removeRoom(roomId,id);
	}

	@GetMapping("{userId}/rooms")
	public List<RoomListDTO> getUserRooms(@PathVariable long userId,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		return roomService.getUserRooms(userId).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}
	
	@GetMapping("/rooms/{roomId}/addInFavourites")
	public List<RoomListDTO> addRoomInFavourites(@PathVariable long roomId,HttpServletRequest request,HttpServletResponse response) throws ElementNotFoundException, UnauthorizedException {
		long id = UserService.authentication(request);
		roomService.addRoomInFavourites(id, roomId);
		return userService.viewFavouriteRooms(id).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@GetMapping("/rooms/cityName={cityName}")
	public List<RoomListDTO> getRoomsByCityName(@PathVariable String cityName) throws ElementNotFoundException {
		return roomService.getRoomsByCityName(cityName).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@GetMapping("/rooms/search")
	public List<RoomListDTO> getRoomsBySearchDTO(@RequestBody SearchRoomDTO searchRoomDTO) throws ElementNotFoundException {
		return roomService.getRoomsBySearchDTO(searchRoomDTO).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}
	
	@PostMapping("/rooms/{roomId}/addPhoto")
	public void addPhoto(@RequestBody PhotoAddDTO photo, @PathVariable long roomId ,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		roomService.addPhoto(roomId, id, photo);
	}
	
	@PostMapping("/rooms/{roomId}/removePhoto/{photoId}")
	public void removePhoto(@PathVariable long roomId ,@PathVariable long photoId ,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		roomService.removePhoto(roomId, id, photoId);
	}

	@GetMapping("/rooms/{roomId}/getInFavourites")
	public List<UserProfileDTO> getInFavourites(@PathVariable long roomId) throws UnauthorizedException, ElementNotFoundException{

		List<UserProfileDTO> userDTO = new LinkedList<>();
		for ( User user : roomService.viewInFavouritesUser(roomId)) {
			userDTO.add(userService.convertUserToDTO(user));
		}
		return userDTO;
	}

	@GetMapping("/rooms/{roomId}/availability")
	public List<LocalDate> getRoomAvailability(@PathVariable long roomId)throws ElementNotFoundException {
		return roomService.getRoomAvailability(roomId);
	}

}
