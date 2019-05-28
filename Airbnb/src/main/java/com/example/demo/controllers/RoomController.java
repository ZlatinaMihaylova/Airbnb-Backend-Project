package com.example.demo.controllers;

import com.example.demo.dto.RoomAddDTO;
import com.example.demo.dto.RoomBookingDTO;
import com.example.demo.dto.RoomInfoDTO;
import com.example.demo.dto.PhotoAddDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;

import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	
	@GetMapping("/rooms/{roomId}/addInFavourites")
	public List<RoomListDTO> addRoomInFavourites(@PathVariable long roomId,HttpServletRequest request,HttpServletResponse response) throws ElementNotFoundException, UnauthorizedException {
		long id = UserController.authentication(request);
		roomService.addRoomInFavourites(id, roomId);
		return userService.viewFavouritesRoom(id).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}
	
	@GetMapping("/rooms")
	public List<RoomListDTO> getAllRooms(HttpServletResponse response) throws ElementNotFoundException{
		return roomService.getAllRooms().stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}
	
	@GetMapping("/rooms/roomId={roomId}")
	public RoomInfoDTO getRoomById(@PathVariable long roomId) throws ElementNotFoundException {
			return roomService.convertRoomToRoomInfoDTO(roomService.getRoomById(roomId));
	}
	
	@PostMapping("/rooms/create")
	public long createRoom(@RequestBody RoomAddDTO newRoom,HttpServletRequest request) throws ElementNotFoundException, UnauthorizedException {
		long id = UserController.authentication(request);
		return roomService.addRoom(newRoom,id);
	}
	
	@PostMapping("/rooms/delete/{roomId}")
	public void deleteRoom(@PathVariable long roomId,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserController.authentication(request);
		roomService.removeRoom(roomId,id);
	}
	
	@GetMapping("/rooms/cityName={cityName}")
	public List<RoomListDTO> getRoomsByCityName(@PathVariable String cityName) throws ElementNotFoundException {
		return roomService.getRoomsByCityName(cityName).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}
	
	@PostMapping("/rooms/booking")
	public long makeReservation(@RequestBody RoomBookingDTO reservation,HttpServletRequest request) throws ElementNotFoundException,UnauthorizedException, BookingIsOverlapingException, BadRequestException {
		long id = UserController.authentication(request);
		return bookingService.makeReservation(reservation, id);
	}
	
	@GetMapping("/rooms/bookings={roomId}")
	public Set<RoomBookingDTO> getAllBookingsForRoom(@PathVariable long roomId) throws ElementNotFoundException{
		return bookingService.getAllBookingsForRoom(roomId).stream().map(booking -> BookingService.convertBookingToDTO(booking)).collect(Collectors.toSet());
	}
	
	@PostMapping("/rooms/{roomId}/addPhoto")
	public long addPhoto(@RequestBody PhotoAddDTO photo, @PathVariable long roomId ,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserController.authentication(request);
		return roomService.addPhoto(roomId, id, photo);
	}
	
	@PostMapping("/rooms/{roomId}/removePhoto/{photoId}")
	public void removePhoto(@PathVariable long roomId ,@PathVariable long photoId ,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserController.authentication(request);
		roomService.removePhoto(roomId, id, photoId);
	}
	
}
