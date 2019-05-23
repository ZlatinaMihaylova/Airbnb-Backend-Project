package com.example.demo.controllers;

import com.example.demo.dto.RoomAddDTO;
import com.example.demo.dto.RoomBookingDTO;
import com.example.demo.dto.RoomInfoDTO;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.PhotoAddDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.service.RoomService;;

@RestController
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@GetMapping("/rooms/{roomId}/addInFavourites")
	public List<RoomListDTO> addRoomInFavourites(@PathVariable long roomId,HttpServletRequest request,HttpServletResponse response) throws ElementNotFoundException, UnauthorizedException {
		long id = UserController.authentication(request);
		return roomService.addRoomInFavourites(id, roomId);
	}
	
	@GetMapping("/rooms")
	public List<RoomListDTO> getAllRooms(HttpServletResponse response) throws ElementNotFoundException{
		List<RoomListDTO> result = roomService.getRoomsForHomePage();
		if (result.isEmpty()) {
			throw new ElementNotFoundException("No rooms to show");
		} else {
			return result;
		}
	}
	
	@GetMapping("/rooms/roomId={roomId}")
	public RoomInfoDTO getRoomById(@PathVariable long roomId) throws ElementNotFoundException {
			return roomService.getRoomById(roomId);
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
	public Set<RoomListDTO> getRoomsByCityName(@PathVariable String cityName){
		return roomService.getRoomsByCityName(cityName);
	}
	
	@PostMapping("/rooms/booking")
	public long makeReservation(@RequestBody RoomBookingDTO reservation,HttpServletRequest request) throws ElementNotFoundException,UnauthorizedException, BookingIsOverlapingException, BadRequestException {
		long id = UserController.authentication(request);
		return roomService.makeReservation(reservation, id);
	}
	
	@GetMapping("/rooms/bookings={roomId}")
	public Set<BookingListDTO> getAllBookingsForRoom(@PathVariable long roomId) throws ElementNotFoundException{
		return roomService.showAllBookingsForRoom(roomId);
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
