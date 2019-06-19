package com.example.demo;


import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.*;
import com.example.demo.service.BookingService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import org.aspectj.apache.bcel.generic.TargetLostException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private  City city = new City(3L, "City", new HashSet<Room>());

    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new HashSet<>(), city,2L, new LinkedList<>());
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
        RoomAddDTO newRoom = new RoomAddDTO("Room",
                "Address", 5, 2,3,4,5, "Details", new HashSet<>(), "City");
        Mockito.when(cityRepository.findByName(newRoom.getCity())).thenReturn(Optional.of(city));
        Room result = roomService.addRoom(newRoom,room.getUserId());

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
    @Test(expected = ElementNotFoundException.class)
    public void deleteRoomNotFound() throws UnauthorizedException, ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.empty());
        roomService.removeRoom(room.getId(),room.getUserId());
        Mockito.verify(roomRepository).delete(room);
    }


    @Test
    public void deleteRoom() throws UnauthorizedException, ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        roomService.removeRoom(room.getId(),room.getUserId());
        Mockito.verify(roomRepository).delete(room);
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
        Assert.assertEquals(rooms, roomService.getUserRooms(user.getId()));
    }


    @Test(expected = UnauthorizedException.class)
    public void addRoomInFavouritesException() throws  ElementNotFoundException, UnauthorizedException{

        User user = new User(2L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234", new LinkedList<>() );
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        roomService.addRoomInFavourites(user.getId(), room.getId());
    }

    @Test
    public void addRoomInFavouritesOK() throws  ElementNotFoundException, UnauthorizedException{
        User user = new User(1L, "FirstName", "LastName", "goodPassword1234",
                "email@gmail.com", LocalDate.now(),"1234", new LinkedList<>() );
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);
        roomService.addRoomInFavourites(user.getId(), room.getId());
        boolean result = room.getInFavourites().contains(user) && user.getFavourites().contains(room);
        Assert.assertTrue(result);
    }

    @Test
    public void getRoomByCityNameTest() {
        Mockito.when(roomRepository.findByCityName(room.getCity().getName())).thenReturn( new LinkedList<>(Arrays.asList(room)));
        Assert.assertEquals(new LinkedList<>(Arrays.asList(room)), roomService.getRoomsByCityName(room.getCity().getName()));
    }

    @Test
    public void getRoomsBySearchDTO() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Mockito.when(bookingService.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>());
        SearchRoomDTO searchRoomDTO = new SearchRoomDTO(room.getCity().getName(),LocalDate.now(),LocalDate.now(), 2);
        Mockito.when(roomRepository.findByCityName(room.getCity().getName())).thenReturn( new LinkedList<>(Arrays.asList(room)));
        Assert.assertEquals(new LinkedList<>(Arrays.asList(room)), roomService.getRoomsBySearchDTO(searchRoomDTO));
    }

    @Test
    public void addPhoto() throws ElementNotFoundException, UnauthorizedException{
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Photo expected = new Photo(1L, "URL",room);
        roomService.addPhoto(room.getId(), room.getUserId(), new PhotoAddDTO("URL"));
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
        roomService.removePhoto(room.getId(),room.getUserId(),photo.getId());
        Mockito.verify(photoRepository).delete(photo);
    }

    @Test(expected = UnauthorizedException.class)
    public void removePhotoUnauthorized() throws ElementNotFoundException, UnauthorizedException {
        Photo photo = new Photo(1L, "url", room);
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        roomService.removePhoto(room.getId(),4L,photo.getId());
        Mockito.verify(photoRepository).delete(photo);
    }


    @Test
    public void removeAllPhoto() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Photo photo1 = new Photo(1L, "url", room);
        Photo photo2 = new Photo(2L, "url", room);
        Photo photo3 = new Photo(3L, "url", room);
        List<Photo> photos = new LinkedList<>(Arrays.asList(photo1,photo2,photo3));
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(photos);

        Method method = roomService.getClass().getDeclaredMethod("removeAllPhotosForRoom", long.class);
        method.setAccessible(true);
        method.invoke(roomService, room.getId());
        Mockito.verify(photoRepository).deleteAll(photos);
    }

    @Test(expected = ElementNotFoundException.class)
    public void checkRoomOwnerElementNotFoundException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Throwable{
        Method method = roomService.getClass().getDeclaredMethod("checkRoomOwner", long.class, long.class);
        method.setAccessible(true);
        try {
            method.invoke(roomService, room.getId(), room.getUserId());
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void checkRoomOwnerUnauthorizedException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Throwable {
        Method method = roomService.getClass().getDeclaredMethod("checkRoomOwner", long.class, long.class);
        method.setAccessible(true);
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        try {
            method.invoke(roomService, room.getId(), 1L);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void checkRoomOwnerSuccessful() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = roomService.getClass().getDeclaredMethod("checkRoomOwner", long.class, long.class);
        method.setAccessible(true);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        method.invoke(roomService, room.getId(), room.getUserId());
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

        RoomListDTO expected = new RoomListDTO(photo.getUrl(),room.getName(),room.getCity().getName(),
                reviewService.getRoomRating(room), reviewService.getRoomTimesRated(room));
        RoomListDTO result = roomService.convertRoomToDTO(room);

        Assert.assertEquals(expected.getMainPhoto(), result.getMainPhoto());
        Assert.assertEquals(expected.getName(), result.getName());
        Assert.assertEquals(expected.getCity(), result.getCity());
        Assert.assertEquals(expected.getRating(), result.getRating(), 2);
        Assert.assertEquals(expected.getTimesRated(), result.getTimesRated());

    }

    @Test
    public void convertRoomToRoomInfoDTO() {
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(photo)));
        Mockito.when(reviewService.getRoomRating(room)).thenReturn(1.0);
        Mockito.when(reviewService.getRoomTimesRated(room)).thenReturn(1);

        RoomInfoDTO expected = new RoomInfoDTO(photo.getUrl(),room.getName(),room.getAddress(),
                room.getGuests(),room.getBedrooms(),room.getBeds(),room.getBaths(),room.getPrice(),
                room.getDetails(), photoRepository.findByRoomId(room.getId()).stream().map(photo -> photo.getUrl()).collect(Collectors.toList()),
                room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toList()));

        RoomInfoDTO result = roomService.convertRoomToRoomInfoDTO(room);

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

        Assert.assertEquals(360, roomService.getRoomAvailability(room.getId()).size());
    }

    @Test
    public void getRoomAvailabilityYear() throws ElementNotFoundException {
        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        Mockito.when(bookingServiceMock.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(
                new Booking(1L, LocalDate.now(), LocalDate.now().plusYears(1), user, room))));

        Assert.assertEquals(0, roomService.getRoomAvailability(room.getId()).size());
    }

    @Test
    public void getMainPhoto() {
        Mockito.when(photoRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(photo)));
        Assert.assertEquals(photo.getUrl(), roomService.getMainPhoto(room.getId()));
    }
}
