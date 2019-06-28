package com.example.demo.ControllersTests;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.BookingService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
public class BookingControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private UserService userService;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private City city;
    private User user;
    private Room room;
    private Booking booking;
    private MockHttpSession session;

    @Before
    public void init() {
        city = new City(3L, "City");
        user = new User(2L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);
        room = new Room(1L, "Room",
                "Address", 5, 2, 3, 4, 5, "Details", new LinkedList<>(), city, 2L, new LinkedList<>());
        booking = new Booking(1L, LocalDate.now(), LocalDate.now().plusDays(1), user, room);
        session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
    }

    @Test
    public void makeReservationShouldPassAndRedirect() throws Exception{
        AddBookingDTO addBookingDTO = new AddBookingDTO(LocalDate.now(), LocalDate.now().plusDays(1).plusDays(1));

        mvc.perform(MockMvcRequestBuilders
                .post("/rooms/roomId={roomId}/bookings", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addBookingDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/myBookings" ));

        ArgumentCaptor<AddBookingDTO> addBookingCaptor = ArgumentCaptor.forClass(AddBookingDTO.class);
        Mockito.verify(bookingService, Mockito.times(1))
                .makeReservation(Mockito.eq(room.getId()), addBookingCaptor.capture(),Mockito.eq(user.getId()));
        Assert.assertEquals(addBookingDTO.getStartDate(), addBookingCaptor.getValue().getStartDate());
        Assert.assertEquals(addBookingDTO.getEndDate(), addBookingCaptor.getValue().getEndDate());
    }

    @Test
    public void makeReservationShouldReturnBadRequestDueToInvalidDates() throws Exception{
        AddBookingDTO addBookingDTO = new AddBookingDTO(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1).plusDays(1));

        mvc.perform(MockMvcRequestBuilders
                .post("/rooms/roomId={roomId}/bookings", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addBookingDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllBookingsForRoomShouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getAllBookingsForRoom(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(booking)));
        Mockito.when(bookingService.convertBookingToDTO(booking))
                .thenReturn(new GetBookingInfoDTO(user.viewAllNames(), booking.getStartDate(), booking.getEndDate(),
                        new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3)));

        mvc.perform(MockMvcRequestBuilders
                .get("/rooms/roomId={roomId}/bookings", room.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].userNames", Matchers.is(user.viewAllNames())))
                .andExpect(jsonPath("$[0].startDate", Matchers.is(booking.getStartDate().toString())))
                .andExpect(jsonPath("$[0].endDate", Matchers.is(booking.getEndDate().toString())))
                .andExpect(jsonPath("$[0].getListOfRoomDTO.mainPhoto", Matchers.is("1")))
                .andExpect(jsonPath("$[0].getListOfRoomDTO.name", Matchers.is(room.getName())))
                .andExpect(jsonPath("$[0].getListOfRoomDTO.city", Matchers.is(room.getCity().getName())))
                .andExpect(jsonPath("$[0].getListOfRoomDTO.rating", Matchers.is(3.4)))
                .andExpect(jsonPath("$[0].getListOfRoomDTO.timesRated", Matchers.is(3)));
    }

}
