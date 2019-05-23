package com.example.demo.service;

import com.example.demo.dao.AmenityRepository;
import com.example.demo.dao.BookingRepository;
import com.example.demo.dao.CityRepository;
import com.example.demo.dao.PhotoRepository;
import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.PhotoAddDTO;
import com.example.demo.dto.ReviewsForRoomDTO;
import com.example.demo.dto.RoomAddDTO;
import com.example.demo.dto.RoomBookingDTO;
import com.example.demo.dto.RoomInfoDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.exceptions.*;
import com.example.demo.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private MessageService messageService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AmenityRepository amenityRepository;
	
	@Autowired
	private BookingRepository bookingRepository;

	public List<RoomListDTO> getRoomsForHomePage() {
		return roomRepository.findAll().stream()
				.map(room -> new RoomListDTO(room.getDetails(), room.getCity().getName(), 
						reviewService.getRoomRating(room.getId()), reviewService.getRoomTimesRated(room.getId())))
				.collect(Collectors.toList());

	}

	public RoomInfoDTO getRoomById(long roomId) throws ElementNotFoundException {
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!"));

		Set<String> amenities = room.getAmenities().stream().map(amenity -> amenity.getName())
				.collect(Collectors.toSet());

		Set<String> photos = photoRepository.findByRoomId(roomId).stream()
				.map(photo -> new String(photo.getUrl())).collect(Collectors.toSet());

		RoomInfoDTO result = new RoomInfoDTO(room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
				room.getPrice(), room.getDetails(), photos, amenities);

		return result;
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
		this.removeAllBookingsFromRoom(roomId,userId);
		this.removeAllPhotosForRoom(roomId);
		reviewService.removeAllReviewsForRoom(roomId);
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found."));

		Set<User> inFavourites = room.getInFavourites();
		for ( User u : inFavourites) {
			u.getFavourites().remove(room);
			userRepository.saveAndFlush(u);
		}

		Set<Amenity> amenities = room.getAmenities();
		for ( Amenity amenity : amenities) {
			amenity.getRooms().remove(room);
			amenityRepository.saveAndFlush(amenity);
		}

		roomRepository.delete(roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!")));
	}

	public Set<RoomListDTO> getUserRooms(long userId) throws ElementNotFoundException{

		if (!userRepository.findById(userId).isPresent()) {
			throw new ElementNotFoundException("User not found");
		}
		return roomRepository.findByUserId(userId).stream()
				.map(room -> new RoomListDTO(room.getDetails(), room.getCity().getName(),
						reviewService.getRoomRating(room.getId()), reviewService.getRoomTimesRated(room.getId())))
				.collect(Collectors.toSet());
/*
		return roomRepository.findAll().stream().filter(room -> room.getUserId().equals(userId))
				.map(room -> new RoomListDTO(room.getDetails(), room.getCity().getName(), 
						reviewService.getRoomRating(room.getId()), reviewService.getRoomTimesRated(room.getId())))
				.collect(Collectors.toSet());
				*/
	}
	
	public Set<ReviewsForRoomDTO> getUserReviews(long userId) throws ElementNotFoundException{
		if (!userRepository.findById(userId).isPresent()) {
			throw new ElementNotFoundException("User not found");
		}
		
		return reviewRepository.findAll().stream().filter(review -> review.getRoom().getUserId() == userId)
				.map(review -> new ReviewsForRoomDTO(review.getUser().viewAllNames(), review.getDate(),review.getText())).collect(Collectors.toSet());
		
				
	}
	
	public List<RoomListDTO> addRoomInFavourites(long userId, long roomId) throws ElementNotFoundException, UnauthorizedException {
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found."));
		if ( room.getUserId() == userId) {
			throw new UnauthorizedException("User can not put his own room in Favourites!");
		}
		User user = userRepository.findById(userId).orElseThrow(() -> new ElementNotFoundException("User not found."));
		room.getInFavourites().add(user);
		roomRepository.saveAndFlush(room);
		user.getFavourites().add(room);
		userRepository.saveAndFlush(user);
		return userService.viewFavouritesRoom(userId);

		
	}
	
	public Set<RoomListDTO> getRoomsByCityName(String cityName) {
		return roomRepository.findAll().stream()
				.filter(room -> room.getCity().getName().equalsIgnoreCase(cityName))
				.map(room -> new RoomListDTO(room.getDetails(), room.getCity().getName(), 
						reviewService.getRoomRating(room.getId()), reviewService.getRoomTimesRated(room.getId())))
				.collect(Collectors.toSet());
	}

	public long makeReservation(RoomBookingDTO reservation, Long userId) throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
		Room room = roomRepository.findById(reservation.getRoomId()).orElseThrow(() -> new ElementNotFoundException("Room not found!"));
		
		if (reservation.getStartDate().isAfter(reservation.getEndDate())) {
			LocalDate temp = reservation.getStartDate();
			reservation.setStartDate(reservation.getEndDate());
			reservation.setEndDate(temp);
		}
		if ( reservation.getStartDate().isBefore(LocalDate.now())) {
			throw new BadRequestException("User can book only for dates after today!");
		}
		if ( room.getUserId().equals(userId)) {
			throw new UnauthorizedException("User can not book hiw own room!");
		}
		Booking result = new Booking(null, reservation.getStartDate(), reservation.getEndDate(),
				userRepository.findById(userId).orElseThrow( () -> new ElementNotFoundException("User not found.")), room);

		boolean isOverlapping = bookingRepository.findByRoomId(reservation.getRoomId()).stream()
				.anyMatch(booking -> booking.overlap(result));
		
		if(isOverlapping) {
			throw new BookingIsOverlapingException("Overlaping dates");
		}
		
		bookingRepository.saveAndFlush(result);
		return result.getId();
		
	}
	
	private void removeAllBookingsFromRoom(long roomId,long userId) throws ElementNotFoundException {
		Set<Booking> roomBookings = bookingRepository.findAll().stream()
		.filter(b -> b.getRoom().getId().equals(roomId))
		.collect(Collectors.toSet());
		
		for ( Booking booking : roomBookings) {
			if ( booking.getStartDate().isAfter(LocalDate.now())) {
				messageService.sendMessage(userId, booking.getUser().getId(),
						"Your booking for " + booking.getRoom().getDetails() + " has been canceled. The room has been deleted");
			}
			
		}
		
		bookingRepository.deleteAll(roomBookings);
	}
	
	public void removeBookingById(long bookingId, long userId, long roomId) throws  ElementNotFoundException, UnauthorizedException{
		this.checkRoomOwner(roomId, userId);
		bookingRepository.delete(bookingRepository.findById(bookingId).orElseThrow(() -> new ElementNotFoundException("Booking not found!")));
	}
	
	public Set<BookingListDTO> showAllBookingsForRoom(long roomId) throws ElementNotFoundException{
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found"));
		return bookingRepository.findByRoomId(roomId).stream()
		.map(b -> new BookingListDTO(b.getStartDate(), b.getEndDate()))
		.collect(Collectors.toSet());
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
	
	public void removeAllPhotosForRoom(long roomId) {
		Set<Photo> photos = photoRepository.findAll().stream()
				.filter(p -> p.getRoom().getId() == roomId)
				.collect(Collectors.toSet());
		
		photoRepository.deleteAll(photos);
	}
	
	private void checkRoomOwner(long roomId, long userId) throws ElementNotFoundException,UnauthorizedException {
		Room room = roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found!"));

		if ( room.getUserId() != userId) {
			throw new UnauthorizedException("User is not authorised");
		}
	}
}
