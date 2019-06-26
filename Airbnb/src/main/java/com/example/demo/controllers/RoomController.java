package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;

import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.validation.executable.ValidateOnExecution;

import com.example.demo.service.RoomService;
import org.springframework.web.servlet.ModelAndView;;

@RestController
public class RoomController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;

	@GetMapping("/rooms/roomId={roomId}")
	public GetRoomInfoDTO getRoomById(@PathVariable long roomId) throws ElementNotFoundException {
		return roomService.convertRoomToRoomInfoDTO(roomService.getRoomById(roomId));
	}

	@GetMapping("/rooms")
	public List<GetListOfRoomDTO> getAllRooms() {
		return roomService.getAllRooms().stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@PostMapping("/rooms/create")
	public ModelAndView addRoom(@RequestBody @Valid AddRoomDTO newRoom, HttpServletRequest request) throws ElementNotFoundException, UnauthorizedException {
		long userId = UserService.authentication(request);
		Room room = roomService.addRoom(newRoom,userId);

		return new ModelAndView("redirect:/rooms/roomId=" + room.getId());

	}

	@DeleteMapping("/rooms/roomId={roomId}/delete")
	public ModelAndView removeRoom(@PathVariable long roomId,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long userId = UserService.authentication(request);
		roomService.removeRoom(roomId ,userId);
		return new ModelAndView("redirect:/userId=" + userId + "/rooms" );
	}

	@GetMapping("/userId={userId}/rooms")
	public List<GetListOfRoomDTO> getUserRooms(@PathVariable long userId) throws UnauthorizedException, ElementNotFoundException {
		return roomService.getUserRooms(userId).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@GetMapping("/rooms/roomId={roomId}/addInFavourites")
	public ModelAndView addRoomInFavourites(@PathVariable long roomId, HttpServletRequest request, HttpServletResponse response) throws ElementNotFoundException, UnauthorizedException {
		long id = UserService.authentication(request);
		roomService.addRoomInFavourites(id, roomId);
		return new ModelAndView("redirect:/viewFavourites");
	}

	@GetMapping("/rooms/cityName={cityName}")
	public List<GetListOfRoomDTO> getRoomsByCityName(@PathVariable @NotEmpty @Size(min = 1) @Valid String cityName) throws ElementNotFoundException {
		return roomService.getRoomsByCityName(cityName).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@GetMapping("/rooms/search")
	public List<GetListOfRoomDTO> getRoomsBySearchDTO(@RequestBody @Valid SearchRoomDTO searchRoomDTO) throws ElementNotFoundException {
		return roomService.getRoomsBySearchDTO(searchRoomDTO).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@PostMapping("/rooms/roomId={roomId}/addPhoto")
	public ModelAndView addPhoto(@RequestBody @Valid AddPhotoDTO photo, @PathVariable long roomId , HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		roomService.addPhoto(roomId, id, photo);
		return new ModelAndView("redirect:/rooms/roomId=" + roomId);
	}

	@PostMapping("/rooms/roomId={roomId}/removePhoto/photoId={photoId}")
	public ModelAndView removePhoto(@PathVariable long roomId ,@PathVariable long photoId ,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		roomService.removePhoto(roomId, id, photoId);
		return new ModelAndView("redirect:/rooms/roomId=" + roomId);
	}

	@GetMapping("/rooms/roomId={roomId}/getInFavourites")
	public List<GetUserProfileDTO> getInFavourites(@PathVariable long roomId) throws UnauthorizedException, ElementNotFoundException{

		List<GetUserProfileDTO> userDTO = new LinkedList<>();
		for ( User user : roomService.viewInFavouritesUser(roomId)) {
			userDTO.add(userService.convertUserToDTO(user));
		}
		return userDTO;
	}

	@GetMapping("/rooms/roomId={roomId}/availability")
	public List<LocalDate> getRoomAvailability(@PathVariable long roomId)throws ElementNotFoundException {
		return roomService.getRoomAvailability(roomId);
	}

}
