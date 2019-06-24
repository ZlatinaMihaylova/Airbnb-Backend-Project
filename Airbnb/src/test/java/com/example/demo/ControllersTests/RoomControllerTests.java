package com.example.demo.ControllersTests;

import com.example.demo.dto.GetRoomInfoDTO;
import com.example.demo.dto.GetListOfRoomDTO;
import com.example.demo.model.City;
import com.example.demo.model.Room;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Arrays;
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

    private Room room;
    private City city;

    @Before
    public void init() {
        city = new City(3L, "City");
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new HashSet<>(), city,2L, new LinkedList<>());

    }

    @Test
    public void getRoomById() throws Exception {
        Mockito.when(roomService.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(roomService.convertRoomToRoomInfoDTO(room))
                .thenReturn(new GetRoomInfoDTO("photo",room.getName(),room.getAddress(), room.getGuests(), room.getBedrooms(), room.getBeds(), room.getBaths(),
                room.getPrice(), room.getDetails(), new LinkedList<>(), new LinkedList<>()));

        mvc.perform( MockMvcRequestBuilders
                .get("/rooms/roomId={roomId}", room.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(room.getName())));
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



}