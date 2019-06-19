package com.example.demo;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dto.ReviewsForRoomDTO;
import com.example.demo.dto.WriteReviewDTO;
import com.example.demo.exceptions.BadRequestException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

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
    private User userWithRoom;
    private Room room;
    private List<Review> reviews;
    private Review review;


    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        userWithRoom = new User(2L, "FirstName1", "LastName1", "goodPassword12341", "email@gmail.com", LocalDate.now(),"1234",null );
        room = new Room(1L, "Room",
                "Address", 5, 2,3,4,5, "Details", new HashSet<>(), new City(),2L, new LinkedList<>());
        reviews = new LinkedList<>(Arrays.asList(
                new Review(1L, LocalDateTime.of(2017, Month.FEBRUARY,3,6,30,40,50000), "Text", user, room, 4),
                new Review(1L, LocalDateTime.of(2017,Month.FEBRUARY,3,6,30,40,50000), "Text", user, room, 5),
                new Review(1L, LocalDateTime.of(2017,Month.FEBRUARY,3,6,30,40,50000), "Text", user, room, 1)));
    }

    @Test
    public void getAllReviewsByRoomId() throws ElementNotFoundException {
        Mockito.when(reviewRepository.findByRoomId(room.getId())).thenReturn(reviews);
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Assert.assertEquals(reviews, reviewService.getAllReviewsByRoomId(room.getId()));
    }

    @Test(expected = UnauthorizedException.class)
    public void addReviewException() throws ElementNotFoundException, UnauthorizedException,BadRequestException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        user.setId(2L);
        reviewService.addReviewForRoom(user.getId(),room.getId(), new WriteReviewDTO());
    }


    @Test(expected = BadRequestException.class)
    public void addReviewStarsException() throws ElementNotFoundException, UnauthorizedException, BadRequestException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);
        WriteReviewDTO writeReviewDTO = new WriteReviewDTO( "review", 6);
        reviewService.addReviewForRoom(user.getId(),room.getId(), writeReviewDTO);
    }

    @Test(expected = BadRequestException.class)
    public void addReviewStarsException2() throws ElementNotFoundException, UnauthorizedException, BadRequestException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);
        WriteReviewDTO writeReviewDTO = new WriteReviewDTO( "review", 0);
        reviewService.addReviewForRoom(user.getId(),room.getId(), writeReviewDTO);
    }

    @Test
    public void addReviewOK() throws ElementNotFoundException, UnauthorizedException, BadRequestException {
        Mockito.when(roomServiceMock.getRoomById(room.getId())).thenReturn(room);
        Mockito.when(userServiceMock.getUserById(user.getId())).thenReturn(user);
        Review expected = new Review(1L,LocalDateTime.now(), "review",user,room, 5);
        reviewService.addReviewForRoom(user.getId(),room.getId(), new WriteReviewDTO( "review", 5));

        ArgumentCaptor<Review> argument = ArgumentCaptor.forClass(Review.class);
        Mockito.verify(reviewRepository).saveAndFlush(argument.capture());

        Assert.assertEquals(expected.getText(), argument.getValue().getText());
        Assert.assertEquals(expected.getUser(), argument.getValue().getUser());
        Assert.assertEquals(expected.getRoom(), argument.getValue().getRoom());
        Assert.assertEquals(expected.getStars(), argument.getValue().getStars());
    }

    @Test
    public void getRoomRating() {
        Mockito.when(reviewRepository.findByRoomId(room.getId()))
                .thenReturn(reviews);
        Assert.assertEquals(3.3, reviewService.getRoomRating(room), 0.1);
    }

    @Test
    public void getRoomTimesRated() {
        Mockito.when(reviewRepository.findByRoomId(room.getId()))
                .thenReturn(reviews);
        Assert.assertEquals(3, reviewService.getRoomTimesRated(room));
    }

    @Test
    public void removeAllReviewsForRoom() {
        Mockito.when(reviewRepository.findByRoomId(room.getId()))
                .thenReturn(reviews);
        reviewService.removeAllReviewsForRoom(room.getId());
        Mockito.verify(reviewRepository).deleteAll(reviews);
    }

    @Test
    public void getAllReviewsForUser() throws ElementNotFoundException {
        Mockito.when(reviewRepository.findAll())
                .thenReturn(reviews);
        Assert.assertEquals(reviews, reviewService.getAllReviewsForUser(userWithRoom.getId()));
    }

    @Test
    public void convertReviewToDTO() throws ElementNotFoundException {
        System.out.println(LocalDateTime.now());
        Review review = reviews.get(1);
        ReviewsForRoomDTO expected = new ReviewsForRoomDTO("FirstName LastName",
                LocalDateTime.of(2017,Month.FEBRUARY,3,6,30,40,50000),"Text");
        ReviewsForRoomDTO result = reviewService.convertReviewToDTO(review);

        Assert.assertEquals(expected.getUserName(), result.getUserName());
        Assert.assertEquals(expected.getDate(), result.getDate());
        Assert.assertEquals(expected.getText(), result.getText());
    }
}
