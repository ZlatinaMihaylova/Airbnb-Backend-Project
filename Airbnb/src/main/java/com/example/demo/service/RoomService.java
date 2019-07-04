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

	public Room addRoom(AddRoomDTO room, User user) throws ElementNotFoundException {
		City city = new City();
		String cityName =  room.getCity().substring(0,1).toUpperCase() + room.getCity().substring(1).toLowerCase();
		city.setName(cityName);
		city.setId(null);
		if (!cityRepository.findByName(cityName).isPresent()) {
			cityRepository.save(city);
		}else {
			city = cityRepository.findByName(cityName).orElseThrow(() -> new ElementNotFoundException("City not found"));
		}
		List<Amenity> amenities = new LinkedList<>();
		for (String amenity : room.getAmenities()){
			Amenity newAmenity = new Amenity(null, amenity.substring(0,1).toUpperCase() + amenity.substring(1).toLowerCase(), new LinkedList<>());
			if (!amenityRepository.findByName(amenity).isPresent()) {
				amenityRepository.save(newAmenity);
			}else {
				newAmenity = amenityRepository.findByName(amenity).orElseThrow(() -> new ElementNotFoundException("Amenity not found"));
			}
			amenities.add(newAmenity);
		}

		Room result = new Room(null, room.getName(), room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(),
				room.getBaths(), room.getPrice(), room.getDetails(), amenities,
				city, user.getId(),null);

		roomRepository.saveAndFlush(result);

		return result;
	}

	public void removeRoom(Room room, User user) throws UnauthorizedException {
		this.checkRoomOwner(room, user);
		bookingService.removeAllBookingsFromRoom(room,user);
		this.removeAllPhotosForRoom(room);
		reviewService.removeAllReviewsForRoom(room);
		for ( User u : room.getInFavourites()) {
			u.getFavourites().remove(room);
			userService.saveUserToDB(u);
		}
		List<Amenity> amenities = room.getAmenities();
		for ( Amenity amenity : amenities) {
			amenity.getRooms().remove(room);
			amenityRepository.saveAndFlush(amenity);
		}
		for ( Amenity amenity : amenities) {
			amenity.getRooms().remove(room);
			amenityRepository.saveAndFlush(amenity);
		}
		roomRepository.delete(room);
	}

	public List<Room> getUserRooms(User user) {
		return roomRepository.findByUserId(user.getId());
	}

	public void addRoomInFavourites(User user, Room room) throws UnauthorizedException {
		if ( room.getUserId() == user.getId()) {
			throw new UnauthorizedException("User can not put his own room in Favourites!");
		}
		room.getInFavourites().add(user);
		roomRepository.saveAndFlush(room);
		user.getFavourites().add(room);
		userService.saveUserToDB(user);
	}

	public List<Room> getRoomsByCityDatesGuests(String city, LocalDate checkin, LocalDate checkout, int guests) {
		return roomRepository
				.findByCityNameContaining(city)
				.stream()
				.filter(room -> room.getGuests() >= guests)
				.filter(room -> isRoomFree(room, checkin, checkout))
				.collect(Collectors.toList());
	}

	public List<Room> filterRoomsByAmenities(List<Room> rooms, List<String> amenitiesNames) throws ElementNotFoundException {
		List<Amenity> amenities = new LinkedList<Amenity>();
		for( String amenity: amenitiesNames) {
			amenities.add(amenityRepository.findByName(amenity).orElseThrow(() -> new ElementNotFoundException("Amenity not found!")));
		}
		return rooms.stream().filter(room -> room.getAmenities().containsAll(amenities) ).collect(Collectors.toList());
	}

	public void addPhoto(Room room, User user , AddPhotoDTO addPhotoDTO) throws UnauthorizedException {
		this.checkRoomOwner(room, user);
		Photo photo = new Photo(null, addPhotoDTO.getUrl(), room);
		photoRepository.saveAndFlush(photo);
	}

	public void removePhoto(Room room, User user , long photoId) throws ElementNotFoundException,UnauthorizedException {
		this.checkRoomOwner(room, user);
		photoRepository.delete(photoRepository.findById(photoId).orElseThrow(() -> new ElementNotFoundException("Photo not found!")));
	}

	private void removeAllPhotosForRoom(Room room) {
		List<Photo> photos = photoRepository.findByRoomId(room.getId());
		photoRepository.deleteAll(photos);
	}

	public List<User> viewInFavouritesUser(Room room) {
		return room.getInFavourites();
	}

	public void checkRoomOwner(Room room, User user) throws UnauthorizedException {
		if ( room.getUserId() != user.getId()) {
			throw new UnauthorizedException("User is not authorised");
		}
	}

	private boolean isRoomFree(Room room, LocalDate startDate, LocalDate endDate) {
		return bookingService.getAllBookingsForRoom(room.getId()).stream().noneMatch(booking -> booking.overlap(startDate, endDate));
	}

	public GetListOfRoomDTO convertRoomToDTO(Room room) {
		return new GetListOfRoomDTO(getMainPhoto(room),room.getName(), room.getCity().getName(),
				reviewService.getRoomRating(room), reviewService.getRoomTimesRated(room));
	}

	public GetRoomInfoDTO convertRoomToRoomInfoDTO(Room room) {
		List<String> amenities = room.getAmenities().stream().map(amenity -> amenity.getName())
				.collect(Collectors.toList());
		List<String> photos = photoRepository.findByRoomId(room.getId()).stream()
				.map(photo -> photo.getUrl()).collect(Collectors.toList());
		return new GetRoomInfoDTO(getMainPhoto(room),room.getName(),room.getCity().getName(),room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
				room.getPrice(), room.getDetails(), photos, amenities,
				reviewService.getAllReviewsByRoom(room).stream().map(review -> reviewService.convertReviewToDTO(review)).collect(Collectors.toList()));
	}

	public List<LocalDate> getRoomAvailability(Room room) {
		return Stream.iterate(LocalDate.now(), date -> date.plusDays(1)).limit(365).filter(day -> isRoomFree(room, day, day)).collect(Collectors.toList());
	}

	public String getMainPhoto(Room room) {
		photoRepository.findByRoomId(room.getId()).stream().findFirst().orElse(new Photo(null, "", room)).getUrl();
		if ( !photoRepository.findByRoomId(room.getId()).isEmpty()){
			return photoRepository.findByRoomId(room.getId()).stream().findFirst().get().getUrl();
		}else {
			return "";
		}
	}

	public List<Room> sortRoomsByRating(List<Room> rooms) {
		Collections.sort(rooms, (r1,r2) -> (int)(reviewService.getRoomBayesianWeightedRating(r2) - reviewService.getRoomBayesianWeightedRating(r1)));
		return rooms;
	}


}
