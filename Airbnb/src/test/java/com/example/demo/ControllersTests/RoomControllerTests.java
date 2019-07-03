package com.example.demo.ControllersTests;

import com.example.demo.dto.*;
import com.example.demo.model.City;
import com.example.demo.model.Photo;
import com.example.demo.model.Room;
import com.example.demo.model.User;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
public class RoomControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private City city;
    private User user;
    private Room room;
    private MockHttpSession session;

    @Before
    public void init() {
        city = new City(3L, "City");
        user = new User(2L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);
        room = new Room(1L, "Room",
                "Address", 5, 2, 3, 4, 5, "Details", new LinkedList<>(), city, 2L, new LinkedList<>());

        session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
    }

    @Test
    public void getRoomByIdOk() throws Exception {
        Mockito.when(roomService.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(roomService.convertRoomToRoomInfoDTO(room))
                .thenReturn(new GetRoomInfoDTO("photo", room.getName(), room.getCity().getName(), room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
                        room.getPrice(), room.getDetails(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>()));

        mvc.perform(MockMvcRequestBuilders
                .get("/rooms/roomId={roomId}", room.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.name", Matchers.is(room.getName())))
                .andExpect(jsonPath("$.address", Matchers.is(room.getAddress())))
                .andExpect(jsonPath("$.guests", Matchers.is(room.getGuests())))
                .andExpect(jsonPath("$.bedrooms", Matchers.is(room.getBedrooms())))
                .andExpect(jsonPath("$.beds", Matchers.is(room.getBeds())))
                .andExpect(jsonPath("$.baths", Matchers.is(room.getBaths())))
                .andExpect(jsonPath("$.price", Matchers.is(room.getPrice())))
                .andExpect(jsonPath("$.details", Matchers.is(room.getDetails())))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllRooms() throws Exception {
        Mockito.when(roomService.getAllRooms()).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));

        mvc.perform(MockMvcRequestBuilders
                .get("/rooms")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void addRoomOK() throws Exception {
        Mockito.when(roomService.addRoom(Mockito.any(AddRoomDTO.class), Mockito.anyLong())).thenReturn(room);

        AddRoomDTO addRoomDTO = new AddRoomDTO("Room", "City",
                "Address", 5, 2, 3, 4, 5, "Details", new LinkedList<>());

        mvc.perform(MockMvcRequestBuilders.post("/rooms/create")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRoomDTO)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/rooms/roomId=" + room.getId()));
    }

    @Test
    public void addRoomShouldReturnBadRequestEmptyName() throws Exception {
        Mockito.when(roomService.addRoom(Mockito.any(AddRoomDTO.class), Mockito.anyLong())).thenReturn(room);

        AddRoomDTO addRoomDTO = new AddRoomDTO("", "City",
                "Address", 5, 2, 3, 4, 5, "Details", new LinkedList<>());


        mvc.perform(MockMvcRequestBuilders.post("/rooms/create").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRoomDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addRoomShouldReturnBadRequestGuestsNotPositive() throws Exception {
        Mockito.when(roomService.addRoom(Mockito.any(AddRoomDTO.class), Mockito.anyLong())).thenReturn(room);

        AddRoomDTO addRoomDTO = new AddRoomDTO("Name", "City",
                "Address", 0, 1, 3, 4, 5, "Details", new LinkedList<>());

        mvc.perform(MockMvcRequestBuilders.post("/rooms/create").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRoomDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeRoomStatusOK() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/rooms/roomId={roomId}/delete", room.getId()).session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/userId=" + user.getId() + "/rooms"));
    }

    @Test
    public void getUserRooms() throws Exception {
        Mockito.when(roomService.getUserRooms(user.getId())).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));

        mvc.perform(MockMvcRequestBuilders.get("/userId={userId}/rooms", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void addRoomInFavouritesShouldPass() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/rooms/roomId={roomId}/addInFavourites", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/viewFavourites"));
        ;
    }
    @Test
    public void getRoomsByCityDatesGuestsShouldReturnOk() throws Exception {
        Mockito.when(roomService.getRoomsByCityDatesGuests(Mockito.any(String.class),Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class), Mockito.any(int.class))).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));

        mvc.perform(MockMvcRequestBuilders.get("/rooms/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", city.getName())
                .param("checkin", LocalDate.now().plusDays(1).toString())
                .param("checkout",LocalDate.now().plusDays(10).toString())
                .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void getRoomsByCityDatesGuestsShouldReturnBadRequestEmptyCityName() throws Exception {
        Mockito.when(roomService.getRoomsByCityDatesGuests(Mockito.any(String.class),
                Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(int.class))).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));

        mvc.perform(MockMvcRequestBuilders.get("/rooms/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", "")
                .param("checkin", LocalDate.now().plusDays(1).toString())
                .param("checkout",LocalDate.now().plusDays(10).toString())
                .param("guests", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRoomsByCityDatesGuestsReturnBadRequestPastDates() throws Exception {
        Mockito.when(roomService.getRoomsByCityDatesGuests(Mockito.any(String.class),
                Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(int.class))).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));

        mvc.perform(MockMvcRequestBuilders.get("/rooms/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", city.getName())
                .param("checkin", LocalDate.now().minusDays(10).toString())
                .param("checkout",LocalDate.now().minusDays(1).toString())
                .param("guests", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRoomsByCityDatesGuestsReturnBadRequestZeroGuests() throws Exception {
        Mockito.when(roomService.getRoomsByCityDatesGuests(Mockito.any(String.class),
                Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(int.class))).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room)).thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));

        mvc.perform(MockMvcRequestBuilders.get("/rooms/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", city.getName())
                .param("checkin", LocalDate.now().plusDays(1).toString())
                .param("checkout",LocalDate.now().plusDays(10).toString())
                .param("guests", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addPhotoWithEmptyUrlShouldReturnBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/rooms/roomId={roomId}/addPhoto", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddPhotoDTO(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addPhotoShouldReturnFoundAndRedirect() throws Exception {
        AddPhotoDTO addPhotoDTO = new AddPhotoDTO("Photo");

        mvc.perform(MockMvcRequestBuilders.post("/rooms/roomId={roomId}/addPhoto", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addPhotoDTO)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/rooms/roomId=" + room.getId()));

        ArgumentCaptor<AddPhotoDTO> photoCaptor = ArgumentCaptor.forClass(AddPhotoDTO.class);
        Mockito.verify(roomService, Mockito.times(1))
                .addPhoto(Mockito.eq(room.getId()), Mockito.eq(user.getId()), photoCaptor.capture());
        Assert.assertEquals(addPhotoDTO.getUrl(), photoCaptor.getValue().getUrl());
    }

    @Test
    public void removePhotoShouldReturnFoundAndRedirect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/rooms/roomId={roomId}/removePhoto/photoId={photoId}", room.getId(), 1L)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/rooms/roomId=" + room.getId()));
    }

    @Test
    public void getInFavouritesOk() throws Exception {
        Mockito.when(roomService.viewInFavouritesUser(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(user)));
        Mockito.when(userService.convertUserToDTO(user)).thenReturn(new GetUserProfileDTO(user.viewAllNames(), user.getPhone(),new LinkedList<>(), new LinkedList<>()));

        mvc.perform(MockMvcRequestBuilders.get("/rooms/roomId={roomId}/getInFavourites", room.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].names", Matchers.is(user.viewAllNames())))
                .andExpect(jsonPath("$[0].phone", Matchers.is(user.getPhone())));
    }

    @Test
    public void getRoomAvailability() throws Exception {
        Mockito.when(roomService.getRoomAvailability(room.getId()))
                .thenReturn(new LinkedList<>(Arrays.asList(LocalDate.now().plusDays(1))));

        mvc.perform(MockMvcRequestBuilders.get("/rooms/roomId={roomId}/availability", room.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.is(LocalDate.now().plusDays(1).toString())));
    }
}