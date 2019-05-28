package com.example.demo.service;

import com.example.demo.dao.AmenityRepository;
import com.example.demo.dao.CityRepository;
import com.example.demo.dao.PhotoRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dto.PhotoAddDTO;
import com.example.demo.dto.RoomAddDTO;
import com.example.demo.dto.RoomInfoDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.exceptions.*;
import com.example.demo.model.*;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private MessageService messageService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;


	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private AmenityRepository amenityRepository;



	public Room getRoomById(long id) throws ElementNotFoundException{
		return roomRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Room not found"));
	}

	public List<Room> getAllRooms() {
		return roomRepository.findAll();
	}

	public long addRoom(RoomAddDTO room, Long userId) throws ElementNotFoundException {
		if (!cityRepository.findByName(room.getCity()).isPresent()) {
			City c = new City();
			c.setName(room.getCity());
			cityRepository.save(c);
		}
		for (String amenity : room.getAmenities()){
			if (!amenityRepository.findByName(amenity).isPresent()) {
				Amenity newAmenity = new Amenity();
				amenity = amenity.substring(0,1).toUpperCase() + amenity.substring(1).toLowerCase();
				newAmenity.setName(amenity);
				amenityRepository.save(newAmenity);
			}
		}
		Set<Amenity> amenities = new HashSet<>();
		for (String amenity : room.getAmenities()){
			amenities.add(amenityRepository.findByName(amenity).orElseThrow(() -> new ElementNotFoundException("Amenity not found")));
		}
		Room result = new Room(null, room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(),
				room.getBaths(), room.getPrice(), room.getDetails(), amenities,
				cityRepository.findByName(room.getCity()).orElseThrow(() -> new ElementNotFoundException("City not found")), userId,null);

		roomRepository.saveAndFlush(result);

		return result.getId();
	}

	public void removeRoom(long roomId, long userId) throws ElementNotFoundException, UnauthorizedException {
		this.checkRoomOwner(roomId, userId);
		bookingService.removeAllBookingsFromRoom(roomId,userId);
		this.removeAllPhotosForRoom(roomId);
		reviewService.removeAllReviewsForRoom(roomId);
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found."));

		Set<User> inFavourites = room.getInFavourites();
		for ( User u : inFavourites) {
			u.getFavourites().remove(room);
			userService.saveUser(u);
		}

		Set<Amenity> amenities = room.getAmenities();
		for ( Amenity amenity : amenities) {
			amenity.getRooms().remove(room);
			amenityRepository.saveAndFlush(amenity);
		}

		roomRepository.delete(roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!")));
	}

	public List<Room> getUserRooms(long userId) throws ElementNotFoundException{
		userService.findById(userId);
		return roomRepository.findByUserId(userId);
	}
	
	public void addRoomInFavourites(long userId, long roomId) throws ElementNotFoundException, UnauthorizedException {
		Room room = getRoomById(roomId);
		if ( room.getUserId() == userId) {
			throw new UnauthorizedException("User can not put his own room in Favourites!");
		}
		User user = userService.findById(userId);
		room.getInFavourites().add(user);
		roomRepository.saveAndFlush(room);
		user.getFavourites().add(room);
		userService.saveUser(user);
	}
	
	public List<Room> getRoomsByCityName(String cityName) {
		return roomRepository.findByCityName(cityName);
	}
	
	public long addPhoto(long roomId, long userId , PhotoAddDTO p) throws ElementNotFoundException, UnauthorizedException {
		this.checkRoomOwner(roomId, userId);
		
		Photo photo = new Photo(null, p.getUrl(), roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!")));
		
		photoRepository.saveAndFlush(photo);
		return photo.getId();
	}
	
	public void removePhoto(long roomId, long userId , long photoId) throws ElementNotFoundException,UnauthorizedException {
		this.checkRoomOwner(roomId, userId);
		photoRepository.delete(photoRepository.findById(photoId).orElseThrow(() -> new ElementNotFoundException("Photo not found!")));
	}
	
	private void removeAllPhotosForRoom(long roomId) {
		Set<Photo> photos = photoRepository.findAll().stream()
				.filter(p -> p.getRoom().getId() == roomId)
				.collect(Collectors.toSet());
		
		photoRepository.deleteAll(photos);
	}
	
	public void checkRoomOwner(long roomId, long userId) throws ElementNotFoundException,UnauthorizedException {
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!"));

		if ( room.getUserId() != userId) {
			throw new UnauthorizedException("User is not authorised");
		}
	}

	public RoomListDTO convertRoomToDTO(Room room) {
		return new RoomListDTO(room.getDetails(), room.getCity().getName(), reviewService.getRoomRating(room), reviewService.getRoomTimesRated(room));
	}


	public RoomInfoDTO convertRoomToRoomInfoDTO(Room room) {
		Set<String> amenities = room.getAmenities().stream().map(amenity -> amenity.getName())
				.collect(Collectors.toSet());

		Set<String> photos = photoRepository.findByRoomId(room.getId()).stream()
				.map(photo -> new String(photo.getUrl())).collect(Collectors.toSet());

		return new RoomInfoDTO(room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
				room.getPrice(), room.getDetails(), photos, amenities);

	}

}
