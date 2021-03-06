package com.example.demo.ServicesTests;

import com.example.demo.dao.BookingRepository;
import com.example.demo.dao.PhotoRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dto.AddBookingDTO;
import com.example.demo.dto.GetBookingInfoDTO;
import com.example.demo.dto.GetListOfRoomDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.*;
import com.example.demo.service.*;
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

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class BookingServiceTests {

    @InjectMocks
    private BookingService bookingService;

    @InjectMocks
    private RoomService roomService;


    @Mock
    private ReviewService reviewServiceMock;

    @Mock
    private RoomService roomServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private BookingService bookingServiceMock;
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MessageService messageService;


    private User user;
    private Room room;
    private List<Booking> bookings;

    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>(), new City(),1L, new LinkedList<>());
        bookings = new LinkedList<>(Arrays.asList(
                new Booking(1L, LocalDate.now().plusDays(4), LocalDate.now().plusDays(6), user, room ),
                new Booking(2L, LocalDate.now().plusDays(6), LocalDate.now().plusDays(10), user, room ),
                new Booking(3L, LocalDate.now().plusDays(12), LocalDate.now().plusDays(13), user, room ),
                new Booking(4L, LocalDate.now().plusDays(20), LocalDate.now().plusDays(25), user, room )));
    }

    @Test
    public void getAllUsersBookings() {
        Mockito.when(bookingRepository.findByUserId(user.getId())).thenReturn(bookings);
        Assert.assertEquals(bookings, bookingService.getAllUsersBookings(user));
    }

    @Test(expected = UnauthorizedException.class)
    public void makeReservationUnauthorised()throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        AddBookingDTO roomBookingDTO = new AddBookingDTO(LocalDate.now().plusDays(4), LocalDate.now().plusDays(6));
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        bookingService.makeReservation(room,roomBookingDTO, user);
    }

    @Test
    public void makeReservationOK()throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);

        User testUser = new User(10L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null);
        AddBookingDTO roomBookingDTO = new AddBookingDTO(LocalDate.now().plusDays(4), LocalDate.now().plusDays(6));
        Booking expected = new Booking(1l, LocalDate.now().plusDays(4), LocalDate.now().plusDays(6), testUser ,room);

        bookingService.makeReservation(room,roomBookingDTO, testUser);
        ArgumentCaptor<Booking> argument = ArgumentCaptor.forClass(Booking.class);
        Mockito.verify(bookingRepository).saveAndFlush(argument.capture());

        Assert.assertEquals(expected.getStartDate(), argument.getValue().getStartDate());
        Assert.assertEquals(expected.getEndDate(), argument.getValue().getEndDate());
        Assert.assertEquals(expected.getUser(), argument.getValue().getUser());
        Assert.assertEquals(expected.getRoom(), argument.getValue().getRoom());
    }

    @Test
    public void removeAllBookingsFromRoom()throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        Mockito.when(bookingRepository.findByRoomId(room.getId()))
                .thenReturn(bookings);
        bookingService.removeAllBookingsFromRoom(room, user);
        Mockito.verify(bookingRepository).deleteAll(bookings);
    }

    @Test
    public void removeBookingById()throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        Mockito.when(bookingRepository.findById(bookings.get(1).getId()))
                .thenReturn(Optional.of(bookings.get(1)));
        bookingService.removeBookingById(bookings.get(1).getId());
        Mockito.verify(bookingRepository).delete(bookings.get(1));
    }

    @Test
    public void getAllBookingsForRoom()throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        Mockito.when(bookingRepository.findByRoomId(room.getId()))
                .thenReturn(bookings);
        Assert.assertEquals(bookings, bookingService.getAllBookingsForRoom(room.getId()));
    }

    @Test
    public void convertBookingToDTO()throws ElementNotFoundException, BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        Mockito.when(roomServiceMock.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("photo",room.getName(),room.getCity().getName(),
                5.0, 50));
        Mockito.when(reviewServiceMock.getRoomRating(room)).thenReturn(5.0);
        Mockito.when(reviewServiceMock.getRoomTimesRated(room)).thenReturn(50);

        Booking booking = bookings.get(1);
        GetBookingInfoDTO expected = new GetBookingInfoDTO(booking.getUser().viewAllNames(),LocalDate.now().plusDays(6), LocalDate.now().plusDays(10), roomService.convertRoomToDTO(room) );
        GetBookingInfoDTO result = bookingService.convertBookingToDTO(booking);

        Assert.assertEquals(expected.getUserNames(), result.getUserNames());
        Assert.assertEquals(expected.getStartDate(), result.getStartDate());
        Assert.assertEquals(expected.getEndDate(), result.getEndDate());
    }

    @Test(expected = BadRequestException.class)
    public void validateReservationDates() throws Throwable, IllegalAccessException,NoSuchMethodException, InvocationTargetException {
        Method method = bookingService.getClass().getDeclaredMethod("validateReservationDates", Booking.class);
        method.setAccessible(true);
        Booking newBooking = new Booking(1L, LocalDate.now().minusDays(4), LocalDate.now().minusDays(6), user, room );

        try {
            method.invoke(bookingService,newBooking );
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void validateReservationSwapDates() throws Throwable, IllegalAccessException,NoSuchMethodException, InvocationTargetException {
        Method method = bookingService.getClass().getDeclaredMethod("validateReservationDates", Booking.class);
        method.setAccessible(true);
        Booking newBooking = new Booking(1L, LocalDate.now().plusDays(6), LocalDate.now().plusDays(4), user, room );
        method.invoke(bookingService,newBooking );

        Assert.assertEquals(LocalDate.now().plusDays(4), newBooking.getStartDate());
        Assert.assertEquals(LocalDate.now().plusDays(6), newBooking.getEndDate());
    }

    @Test(expected = BookingIsOverlapingException.class)
    public void checkForOverlappingDates() throws Throwable, IllegalAccessException,NoSuchMethodException, InvocationTargetException {
        Mockito.when(bookingRepository.findByRoomId(room.getId())).thenReturn(bookings);
        Method method = bookingService.getClass().getDeclaredMethod("checkForOverlappingDates", Booking.class);
        method.setAccessible(true);
        Booking newBooking = new Booking(1L, LocalDate.now().plusDays(21), LocalDate.now().plusDays(23), user, room );

        try {
            method.invoke(bookingService,newBooking);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void checkForOverlappingDatesOK() throws Throwable, IllegalAccessException,NoSuchMethodException, InvocationTargetException {
        Mockito.when(bookingRepository.findByRoomId(room.getId())).thenReturn(bookings);
        Method method = bookingService.getClass().getDeclaredMethod("checkForOverlappingDates", Booking.class);
        method.setAccessible(true);
        Booking newBooking = new Booking(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), user, room );
        try {
            method.invoke(bookingService,newBooking);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
