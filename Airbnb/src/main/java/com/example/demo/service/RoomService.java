package com.example.demo.service;

import com.example.demo.dao.AmenityRepository;
import com.example.demo.dao.CityRepository;
import com.example.demo.dao.PhotoRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dto.*;
import com.example.demo.exceptions.*;
import com.example.demo.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	public Room addRoom(RoomAddDTO room, Long userId) throws ElementNotFoundException {
		if (!cityRepository.findByName(room.getCity()).isPresent()) {
			City c = new City();
			c.setName(room.getCity());
			cityRepository.save(c);
		}
		Set<Amenity> amenities = new HashSet<>();
		for (String amenity : room.getAmenities()){
			if (!amenityRepository.findByName(amenity).isPresent()) {
				Amenity newAmenity = new Amenity();
				amenity = amenity.substring(0,1).toUpperCase() + amenity.substring(1).toLowerCase();
				newAmenity.setName(amenity);
				amenityRepository.save(newAmenity);
			}
			amenities.add(amenityRepository.findByName(amenity).orElseThrow(() -> new ElementNotFoundException("Amenity not found")));
		}

		Room result = new Room(null, room.getName(), room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(),
				room.getBaths(), room.getPrice(), room.getDetails(), amenities,
				cityRepository.findByName(room.getCity()).orElseThrow(() -> new ElementNotFoundException("City not found")), userId,null);

		roomRepository.saveAndFlush(result);

		return result;
	}

	public void removeRoom(long roomId, long userId) throws ElementNotFoundException, UnauthorizedException {
		this.checkRoomOwner(roomId, userId);
		bookingService.removeAllBookingsFromRoom(roomId,userId);
		this.removeAllPhotosForRoom(roomId);
		reviewService.removeAllReviewsForRoom(roomId);
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found."));
		List<User> inFavourites = room.getInFavourites();
		for ( User u : inFavourites) {
			u.getFavourites().remove(room);
			userService.saveUserToDB(u);
		}
		Set<Amenity> amenities = room.getAmenities();
		for ( Amenity amenity : amenities) {
			amenity.getRooms().remove(room);
			amenityRepository.saveAndFlush(amenity);
		}
		roomRepository.delete(roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!")));
	}

	public List<Room> getUserRooms(long userId) {
		return roomRepository.findByUserId(userId);
	}
	
	public void addRoomInFavourites(long userId, long roomId) throws ElementNotFoundException,UnauthorizedException {
		Room room = getRoomById(roomId);
		if ( room.getUserId() == userId) {
			throw new UnauthorizedException("User can not put his own room in Favourites!");
		}
		User user = userService.getUserById(userId);
		room.getInFavourites().add(user);
		roomRepository.saveAndFlush(room);
		user.getFavourites().add(room);
		userService.saveUserToDB(user);
	}
	
	public List<Room> getRoomsByCityName(String cityName) {
		return roomRepository.findByCityName(cityName);
	}

	public List<Room> getRoomsBySearchDTO(SearchRoomDTO searchRoomDTO) {
		return roomRepository
				.findByCityName(searchRoomDTO.getCity())
				.stream()
				.filter(room -> isRoomFree(room, searchRoomDTO.getStartDate(), searchRoomDTO.getEndDate()))
				.filter(room -> room.getGuests() >= searchRoomDTO.getGuests())
				.collect(Collectors.toList());
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
		List<Photo> photos = photoRepository.findByRoomId(roomId);
		photoRepository.deleteAll(photos);
	}

	public List<User> viewInFavouritesUser(long roomId) throws ElementNotFoundException {
		Room room = getRoomById(roomId);
		return room.getInFavourites();
	}
	
	void checkRoomOwner(long roomId, long userId) throws ElementNotFoundException,UnauthorizedException {
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!"));
		if ( room.getUserId() != userId) {
			throw new UnauthorizedException("User is not authorised");
		}
	}

	private boolean isRoomFree(Room room, LocalDate startDate, LocalDate endDate) {
		return bookingService.getAllBookingsForRoom(room.getId()).stream().noneMatch(booking -> booking.overlap(startDate, endDate));
	}

	public RoomListDTO convertRoomToDTO(Room room) {
		return new RoomListDTO(getMainPhoto(room.getId()),room.getName(), room.getCity().getName(),
				reviewService.getRoomRating(room), reviewService.getRoomTimesRated(room));
	}

	public RoomInfoDTO convertRoomToRoomInfoDTO(Room room) {
		List<String> amenities = room.getAmenities().stream().map(amenity -> amenity.getName())
				.collect(Collectors.toList());
		List<String> photos = photoRepository.findByRoomId(room.getId()).stream()
				.map(photo -> photo.getUrl()).collect(Collectors.toList());
		return new RoomInfoDTO(getMainPhoto(room.getId()),room.getName(),room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
				room.getPrice(), room.getDetails(), photos, amenities);
	}

	public List<LocalDate> getRoomAvailability(long roomId) throws  ElementNotFoundException {
		LocalDate now = LocalDate.now();
		Room room = getRoomById(roomId);
		return Stream.iterate(now, date -> date.plusDays(1)).limit(365).filter(day -> isRoomFree(room, day, day)).collect(Collectors.toList());
	}
	public String getMainPhoto(long roomId) {
		if ( !photoRepository.findByRoomId(roomId).isEmpty()){
			return photoRepository.findByRoomId(roomId).stream().findFirst().get().getUrl();
		}else {
			return "";
		}
	}
}
