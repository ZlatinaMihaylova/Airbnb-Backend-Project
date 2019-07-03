package com.example.demo.ControllersTests;

import com.example.demo.dto.AddPhotoDTO;
import com.example.demo.dto.AddReviewDTO;
import com.example.demo.dto.GetListOfRoomDTO;
import com.example.demo.dto.GetReviewsForRoomDTO;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.City;
import com.example.demo.model.Review;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
public class ReviewControllerTests {

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
    private Review review;
    private MockHttpSession session;
    private LocalDateTime localDateTime;

    @Before
    public void init() {
        city = new City(3L, "City");
        user = new User(2L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);
        room = new Room(1L, "Room",
                "Address", 5, 2, 3, 4, 5, "Details", new LinkedList<>(), city, 2L, new LinkedList<>());
        localDateTime = LocalDateTime.of(2017,12,12, 12,12, 12);
        review = new Review(1L,localDateTime , "Text", user, room, 5);
        session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
    }

    @Test
    public void getAllReviewsByRoomIdShouldReturnReview() throws Exception {
        Mockito.when(reviewService.getAllReviewsByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(review)));
        Mockito.when(reviewService.convertReviewToDTO(review)).thenReturn(new GetReviewsForRoomDTO(user.viewAllNames(), review.getDate(),review.getStars(), review.getText()));

        mvc.perform(MockMvcRequestBuilders
                .get("/rooms/roomId={roomId}/reviews", room.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].userName", Matchers.is(user.viewAllNames())))
                .andExpect(jsonPath("$[0].date", Matchers.is(localDateTime.toString())))
                .andExpect(jsonPath("$[0].stars", Matchers.is(review.getStars())))
                .andExpect(jsonPath("$[0].text", Matchers.is(review.getText())));
    }

    @Test
    public void addReviewForRoomShouldPass() throws Exception {
        AddReviewDTO addReviewDTO = new AddReviewDTO("Text", 4);

        mvc.perform(MockMvcRequestBuilders
                .post("/rooms/roomId={roomId}/reviews", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReviewDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/rooms/" + room.getId() +"/reviews" ));

        ArgumentCaptor<AddReviewDTO> addReviewCaptor = ArgumentCaptor.forClass(AddReviewDTO.class);
        Mockito.verify(reviewService, Mockito.times(1))
                .addReviewForRoom(Mockito.eq(user.getId()), Mockito.eq(room.getId()), addReviewCaptor.capture());
        Assert.assertEquals(addReviewDTO.getText(), addReviewCaptor.getValue().getText());
        Assert.assertEquals(addReviewDTO.getStars(), addReviewCaptor.getValue().getStars());
    }

    @Test
    public void addReviewForRoomShouldReturnBadRequestDueToEmptyText() throws Exception {
        AddReviewDTO addReviewDTO = new AddReviewDTO("", 4);

        mvc.perform(MockMvcRequestBuilders
                .post("/rooms/roomId={roomId}/reviews", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReviewDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void addReviewForRoomShouldReturnBadRequestDueToMinStars() throws Exception {
        AddReviewDTO addReviewDTO = new AddReviewDTO("text", 0);

        mvc.perform(MockMvcRequestBuilders
                .post("/rooms/roomId={roomId}/reviews", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReviewDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void addReviewForRoomShouldReturnBadRequestDueToMaxStars() throws Exception {
        AddReviewDTO addReviewDTO = new AddReviewDTO("text", 6);

        mvc.perform(MockMvcRequestBuilders
                .post("/rooms/roomId={roomId}/reviews", room.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReviewDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }
}
