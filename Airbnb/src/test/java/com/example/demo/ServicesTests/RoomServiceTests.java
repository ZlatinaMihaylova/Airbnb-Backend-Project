package com.example.demo.ServicesTests;


import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.*;
import com.example.demo.service.BookingService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class RoomServiceTests {

    @Mock
    private BookingService bookingServiceMock;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserService userServiceMock;

    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private ReviewService reviewService;


    @Mock
    private RoomService roomServiceMock;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    private Room room;
    private User user;
    private Photo photo;
    private  City city = new City(3L, "City");

    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>(), city,1L, new LinkedList<>());
        photo = new Photo(1L, "url", room);
    }


    @Test(expected = ElementNotFoundException.class)
    public void findRoomByIdNotFound() throws ElementNotFoundException  {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.empty());
        roomService.getRoomById(room.getId());
    }

    @Test
    public void findRoomById() throws ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Assert.assertEquals(room, roomService.getRoomById(room.getId()));
    }

    @Test
    public void findAllRooms() {
        List<Room> allRooms = new LinkedList<>();
        allRooms.add(new Room());
        allRooms.add(new Room());
        allRooms.add(new Room());

        Mockito.when(roomRepository.findAll()).thenReturn(allRooms);
        Assert.assertEquals(allRooms, roomService.getAllRooms());
    }

    @Test
    public void addRoom() throws ElementNotFoundException {
        AddRoomDTO newRoom = new AddRoomDTO("Room", "City",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>());
        Mockito.when(cityRepository.findByName(newRoom.getCity())).thenReturn(Optional.of(city));
        Room result = roomService.addRoom(newRoom, user);

        Assert.assertEquals(room.getUserId(),result.getUserId());
        Assert.assertEquals(room.getName(),result.getName());
        Assert.assertEquals(room.getAddress(),result.getAddress());
        Assert.assertEquals(room.getGuests(),result.getGuests());
        Assert.assertEquals(room.getBedrooms(),result.getBedrooms());
        Assert.assertEquals(room.getBeds(),result.getBeds());
        Assert.assertEquals(room.getBaths(),result.getBaths());
        Assert.assertEquals(room.getPrice(),result.getPrice());
        Assert.assertEquals(room.getDetails(),result.getDetails());
        Assert.assertEquals(room.getCity(),result.getCity());
    }

    @Test
    public void addRoomShouldSaveCity() throws ElementNotFoundException {
        AddRoomDTO newRoom = new AddRoomDTO("Room", "City",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>());
        Mockito.when(cityRepository.findByName(newRoom.getCity())).thenReturn(Optional.empty());
        City newCity = new City(1L, newRoom.getCity());
        roomService.addRoom(newRoom, user);
        Mockito.verify(cityRepository).save(newCity);

    }

    @Test
    public void addRoomShouldSaveAmenity() throws ElementNotFoundException {
        AddRoomDTO newRoom = new AddRoomDTO("Room", "City",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>(Arrays.asList("Amenity")));
        Mockito.when(cityRepository.findByName(newRoom.getCity())).thenReturn(Optional.of(city));
        Amenity newAmenity = new Amenity(1L, newRoom.getAmenities().get(0), new LinkedList<>() );
        roomService.addRoom(newRoom, user);
        Mockito.verify(amenityRepository).save(newAmenity);

    }

    @Test
    public void deleteRoom() throws UnauthorizedException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        roomService.removeRoom(room, user);

    }

    @Test
    public void deleteRoomShouldRemoveRoomFromFavourites() throws UnauthorizedException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        User user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",new LinkedList<>(Arrays.asList(room)));
        room.getInFavourites().add(user);
        roomService.removeRoom(room, user);
        Mockito.verify(roomRepository).delete(room);
        Assert.assertTrue(user.getFavourites().isEmpty());
    }

    @Test
    public void deleteRoomShouldRemoveRoomFromAmenities() throws UnauthorizedException, ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        User user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",new LinkedList<>(Arrays.asList(room)));
        room.getInFavourites().add(user);
        roomService.removeRoom(room, user);
        Mockito.verify(roomRepository).delete(room);
        Assert.assertTrue(user.getFavourites().isEmpty());
    }


    @Test
    public void getUserRooms() throws UnauthorizedException, ElementNotFoundException {
        User user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        List<Room> rooms = new LinkedList<>();
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());
        Mockito.when(roomRepository.findByUserId(user.getId())).thenReturn(rooms);
        Assert.assertEquals(rooms, roomService.getUserRooms(user));
    }


    @Test(expected = UnauthorizedException.class)
    public void addRoomInFavouritesException() throws UnauthorizedException{
        roomService.addRoomInFavourites(user, room);
    }

    @Test
    public void addRoomInFavouritesOK() throws  ElementNotFoundException, UnauthorizedException{
        User user = new User(3L, "FirstName", "LastName", "goodPassword1234",
                "email@gmail.com", LocalDate.now(),"1234", new LinkedList<>() );
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);
        roomService.addRoomInFavourites(user, room);
        boolean result = room.getInFavourites().contains(user) && user.getFavourites().contains(room);
        Assert.assertTrue(result);
    }

    @Test
    public void getRoomsBySearchDTO() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Mockito.when(bookingService.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>());
        Mockito.when(roomRepository.findByCityNameContaining(room.getCity().getName())).thenReturn( new LinkedList<>(Arrays.asList(room)));
        Assert.assertEquals(new LinkedList<>(Arrays.asList(room)),
                roomService.getRoomsByCityDatesGuests(room.getCity().getName(), LocalDate.now(),LocalDate.now().plusDays(1), 2));
    }

    @Test
    public void addPhoto() throws ElementNotFoundException, UnauthorizedException{
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Photo expected = new Photo(user.getId(),"URL", room);
        roomService.addPhoto(room, user, new AddPhotoDTO("URL"));

        ArgumentCaptor<Photo> argument = ArgumentCaptor.forClass(Photo.class);
        Mockito.verify(photoRepository).saveAndFlush(argument.capture());

        Assert.assertEquals(expected.getUrl(), argument.getValue().getUrl());
        Assert.assertEquals(expected.getRoom(), argument.getValue().getRoom());
    }

    @Test
    public void removePhoto() throws ElementNotFoundException, UnauthorizedException {
        Photo photo = new Photo(1L, "url", room);
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        roomService.removePhoto(room, user,photo.getId());
        Mockito.verify(photoRepository).delete(photo);
    }

    @Test(expected = UnauthorizedException.class)
    public void removePhotoUnauthorized() throws ElementNotFoundException, UnauthorizedException {
        Photo photo = new Photo(1L, "url", room);
        User user = new User();
        user.setId(4L);
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        roomService.removePhoto(room, user, photo.getId());
        Mockito.verify(photoRepository).delete(photo);
    }

    @Test
    public void removeAllPhotos() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Photo photo1 = new Photo(1L, "url", room);
        Photo photo2 = new Photo(2L, "url", room);
        Photo photo3 = new Photo(3L, "url", room);
        List<Photo> photos = new LinkedList<>(Arrays.asList(photo1,photo2,photo3));
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(photos);

        Method method = roomService.getClass().getDeclaredMethod("removeAllPhotosForRoom", Room.class);
        method.setAccessible(true);
        method.invoke(roomService, room);
        Mockito.verify(photoRepository).deleteAll(photos);
    }

    @Test(expected = UnauthorizedException.class)
    public void checkRoomOwnerUnauthorizedException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Throwable {
        Method method = roomService.getClass().getDeclaredMethod("checkRoomOwner", Room.class, User.class);
        method.setAccessible(true);

        try {
            method.invoke(roomService, room, new User());
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void checkRoomOwnerSuccessful() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = roomService.getClass().getDeclaredMethod("checkRoomOwner", Room.class, User.class);
        method.setAccessible(true);

        method.invoke(roomService, room, user);
    }

    @Test
    public void isRoomFree() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = roomService.getClass().getDeclaredMethod("isRoomFree", Room.class, LocalDate.class, LocalDate.class);
        method.setAccessible(true);

        Mockito.when(bookingService.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>(
                Arrays.asList(new Booking(1L, LocalDate.now().minusMonths(1), LocalDate.now(), user, room))));

        Assert.assertTrue((boolean) method.invoke(roomService, room, LocalDate.now().minusMonths(3), LocalDate.now().minusMonths(2)));
        Assert.assertTrue((boolean) method.invoke(roomService, room, LocalDate.now().minusDays(1), LocalDate.now()));
    }

    @Test
    public void convertRoomToDTO() {
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(photo)));
        Mockito.when(reviewService.getRoomRating(room)).thenReturn(1.0);
        Mockito.when(reviewService.getRoomTimesRated(room)).thenReturn(1);

        GetListOfRoomDTO expected = new GetListOfRoomDTO(photo.getUrl(),room.getName(),room.getCity().getName(),
                reviewService.getRoomRating(room), reviewService.getRoomTimesRated(room));
        GetListOfRoomDTO result = roomService.convertRoomToDTO(room);

        Assert.assertEquals(expected.getMainPhoto(), result.getMainPhoto());
        Assert.assertEquals(expected.getName(), result.getName());
        Assert.assertEquals(expected.getCity(), result.getCity());
        Assert.assertEquals(expected.getRating(), result.getRating(), 2);
        Assert.assertEquals(expected.getTimesRated(), result.getTimesRated());

    }

    @Test
    public void convertRoomToRoomInfoDTO() throws ElementNotFoundException{
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(photo)));
        Mockito.when(reviewService.getRoomRating(room)).thenReturn(1.0);
        Mockito.when(reviewService.getAllReviewsByRoom(room)).thenReturn(new LinkedList<>());
        Mockito.when(reviewService.getRoomTimesRated(room)).thenReturn(1);

        GetRoomInfoDTO expected = new GetRoomInfoDTO(photo.getUrl(),room.getName(),room.getCity().getName(),room.getAddress(),
                room.getGuests(),room.getBedrooms(),room.getBeds(),room.getBaths(),room.getPrice(),
                room.getDetails(), photoRepository.findByRoomId(room.getId()).stream().map(photo -> photo.getUrl()).collect(Collectors.toList()),
                room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toList()),
                new LinkedList<>());

        GetRoomInfoDTO result = roomService.convertRoomToRoomInfoDTO(room);

        Assert.assertEquals(expected.getMainPhoto(), result.getMainPhoto());
        Assert.assertEquals(expected.getName(), result.getName());
        Assert.assertEquals(expected.getAddress(), result.getAddress());
        Assert.assertEquals(expected.getGuests(), result.getGuests());
        Assert.assertEquals(expected.getBedrooms(), result.getBedrooms());
        Assert.assertEquals(expected.getBeds(), result.getBeds());
        Assert.assertEquals(expected.getBaths(), result.getBaths());
        Assert.assertEquals(expected.getPrice(), result.getPrice());
        Assert.assertEquals(expected.getDetails(), result.getDetails());
        Assert.assertEquals(expected.getPhotos(), result.getPhotos());
        Assert.assertEquals(expected.getAmenities(), result.getAmenities());
    }

    @Test
    public void getRoomAvailability() throws ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        Mockito.when(bookingServiceMock.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(
                new Booking(1L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), user, room),
                new Booking(1L, LocalDate.now().plusDays(4), LocalDate.now().plusDays(7), user, room))));

        Assert.assertEquals(360, roomService.getRoomAvailability(room).size());
    }

    @Test
    public void getRoomAvailabilityYear() throws ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        Mockito.when(bookingServiceMock.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(
                new Booking(1L, LocalDate.now(), LocalDate.now().plusYears(1), user, room))));

        Assert.assertEquals(0, roomService.getRoomAvailability(room).size());
    }

    @Test
    public void getMainPhoto() {
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(photo)));
        Assert.assertEquals(photo.getUrl(), roomService.getMainPhoto(room));
    }


}
