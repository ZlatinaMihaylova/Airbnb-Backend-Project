package com.example.demo.ControllersTests;

import com.example.demo.dto.AddRoomDTO;
import com.example.demo.dto.GetRoomInfoDTO;
import com.example.demo.dto.GetListOfRoomDTO;
import com.example.demo.model.City;
import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private MockHttpSession session;

    private Room room;
    private City city;
    private User user;

    @Before
    public void init() {
        city = new City(3L, "City");
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>(), city,2L, new LinkedList<>());
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );

        session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
    }

    @Test
    public void getRoomByIdOk() throws Exception {
        Mockito.when(roomService.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(roomService.convertRoomToRoomInfoDTO(room))
                .thenReturn(new GetRoomInfoDTO("photo",room.getName(),room.getCity().getName(),room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
                room.getPrice(), room.getDetails(), new LinkedList<>(), new LinkedList<>()));

        mvc.perform( MockMvcRequestBuilders
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

        mvc.perform( MockMvcRequestBuilders
                .get("/rooms")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void addRoomOK() throws Exception {
        Mockito.when(roomService.convertRoomToRoomInfoDTO(room)).thenReturn(new GetRoomInfoDTO("photo",room.getName(),room.getCity().getName(),room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
                room.getPrice(), room.getDetails(), new LinkedList<>(), new LinkedList<>()));;

        AddRoomDTO addRoomDTO = new AddRoomDTO("Room", "City",
                "Address", 5, 2,3,4,5, "Details", new LinkedList<>());
        Mockito.when(roomService.addRoom(addRoomDTO, user.getId())).thenReturn(room);

        MvcResult result = mvc.perform( MockMvcRequestBuilders
                .post("/rooms/create").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(addRoomDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        System.out.println(roomService.convertRoomToRoomInfoDTO(roomService.addRoom(addRoomDTO,user.getId())));
    }

}