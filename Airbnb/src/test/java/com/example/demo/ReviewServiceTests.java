package com.example.demo;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dto.WriteReviewDTO;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.*;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class ReviewServiceTests {

    @InjectMocks
    private ReviewService  reviewService;

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomService roomServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ReviewService reviewServiceMock;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ReviewRepository reviewRepository;


    private User user;
    private Room room;
    private Review review;


    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new HashSet<>(), new City(),2L, new LinkedList<>());
        review = new Review(1L, LocalDateTime.now(), "Text", user, room, 5);
    }

    @Test(expected = ElementNotFoundException.class)
    public void getAllReviewsByRoomIdException() throws ElementNotFoundException {
        Mockito.when(reviewRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(review)));
        Assert.assertEquals(new LinkedList<>(Arrays.asList(review)), reviewService.getAllReviewsByRoomId(room.getId()));
    }

    @Test
    public void getAllReviewsByRoomId() throws ElementNotFoundException {
        Mockito.when(reviewRepository.findByRoomId(room.getId())).thenReturn(new LinkedList<>(Arrays.asList(review)));
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Assert.assertEquals(new LinkedList<>(Arrays.asList(review)), reviewService.getAllReviewsByRoomId(room.getId()));
    }

    @Test(expected = UnauthorizedException.class)
    public void addReviewException() throws ElementNotFoundException, UnauthorizedException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        user.setId(2L);
        reviewService.addReviewForRoom(user.getId(),room.getId(), new WriteReviewDTO());
    }

    @Test
    public void addReviewOK() throws ElementNotFoundException, UnauthorizedException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);
        reviewService.addReviewForRoom(user.getId(),room.getId(), new WriteReviewDTO( "review", 5));
    }


}
