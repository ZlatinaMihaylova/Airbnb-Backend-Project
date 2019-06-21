package com.example.demo.ControllersTests;

import com.example.demo.AirbnbBApplication;
import com.example.demo.controllers.RoomController;
import com.example.demo.model.City;
import com.example.demo.model.Room;
import com.example.demo.service.RoomService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationContext.class)
@WebMvcTest
public class RoomControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomController roomController;

    private Room room;
    private City city;

    @Before
    public void init() {
        city = new City(3L, "City", new HashSet<Room>());
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new HashSet<>(), city,2L, new LinkedList<>());

    }

    @Test
    public void getAllRooms() throws Exception {
        Mockito.when(roomService.getAllRooms()).thenReturn(new LinkedList<>(Arrays.asList(room)));

        mvc.perform( MockMvcRequestBuilders
                .get("/rooms")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }


}