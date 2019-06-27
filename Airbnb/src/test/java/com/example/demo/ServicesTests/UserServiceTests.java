package com.example.demo.ServicesTests;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.*;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Room;
import com.example.demo.model.User;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomService roomServiceMock;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private User user;
    private EditProfileDTO editProfileDTO;

    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
        editProfileDTO = new EditProfileDTO("NewFirstName",
                "NewLastName", "newGoodPassword1234", "NewEmail@gmail.com", LocalDate.now().minusMonths(2),"new1234");
    }

    @Test
    public void saveUserToDBTest() {
        userService.saveUserToDB(user);
        Mockito.verify(userRepository).saveAndFlush(user);
    }

    @Test
    public void getAllUsersTest() {
        Mockito.when(userRepository.findAll()).thenReturn(new LinkedList<>(Arrays.asList(user)));
        Assert.assertEquals(new LinkedList<>(Arrays.asList(user)), userService.getAllUsers());
    }

    @Test(expected = ElementNotFoundException.class)
    public void findUserByIdNotFound() throws ElementNotFoundException  {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Mockito.when(roomService.getUserRooms(user.getId())).thenReturn((new LinkedList<>()));
        userService.getUserById(user.getId());
    }

    @Test
    public void findUserById() throws ElementNotFoundException {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(roomService.getUserRooms(user.getId())).thenReturn(new LinkedList<>());
        Mockito.when(reviewService.getAllReviewsForUser(user.getId())).thenReturn(new LinkedList<>());
        Assert.assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test(expected = SignUpException.class)
    public void testInvalidPassword() throws BadRequestException, SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
        userService.signUp(new SignUpDTO("FirstName", "LastName", "1234", "email@gmail.com", LocalDate.now(),"1234"), new MockHttpSession());
    }

    @Test(expected = SignUpException.class)
    public void testInvalidEmail() throws BadRequestException, SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
        userService.signUp(new SignUpDTO("FirstName", "LastName", "goodPassword1234", "email", LocalDate.now(),"1234" ),new MockHttpSession());
    }

    @Test(expected = SignUpException.class)
    public void testAlreadyUsedEmail() throws BadRequestException, SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        userService.signUp(new SignUpDTO("FirstName", "LastName", "goodPassword1234", "email", LocalDate.now(),"1234"),new MockHttpSession());
    }

    @Test
    public void testSignUpOK() throws BadRequestException, SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException  {

        userService.signUp(new SignUpDTO( "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234"),new MockHttpSession());
        Mockito.verify(userRepository).saveAndFlush(user);
    }

    @Test(expected = ElementNotFoundException.class)
    public void loginUserNotFound() throws BadRequestException, ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        userService.login(new LoginDTO("email@abv.bg", "12345" ),new MockHttpSession());
    }

    @Test
    public void loginSuccessful() throws BadRequestException, ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = userService.getClass().getDeclaredMethod("encryptPassword", String.class);
        method.setAccessible(true);
        Mockito.when(userRepository.findByEmailAndPassword(user.getEmail(),method.invoke(userService,user.getPassword()).toString())).thenReturn(Optional.of(user));
        Assert.assertEquals(user,userService.login(new LoginDTO(user.getEmail(),user.getPassword()),new MockHttpSession()));
    }

    @Test(expected = BadRequestException.class)
    public void changeInformationBadEmailTest() throws BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchMethodException,InvocationTargetException,IllegalAccessException {
        User editedUser = userService.changeInformation(1L,
                new EditProfileDTO("NewFirstName",
                        "NewLastName", "newGoodPassword1234", "bad email", LocalDate.now().minusMonths(2),"new1234"));

    }

    @Test(expected = BadRequestException.class)
    public void changeInformationBadPasswordTest() throws BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchMethodException,InvocationTargetException,IllegalAccessException {
        User editedUser = userService.changeInformation(1L,
                new EditProfileDTO("NewFirstName",
                        "NewLastName", "1234", "NewEmail@gmail.com", LocalDate.now().minusMonths(2),"new1234"));

    }

    @Test(expected = BadRequestException.class)
    public void changeEmailAlreadyUsedTest() throws BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchMethodException,InvocationTargetException,IllegalAccessException {
        Mockito.when(userRepository.findByEmail(editProfileDTO.getEmail())).thenReturn(Optional.of(user));
        User editedUser = userService.changeInformation(1L, editProfileDTO);
    }

    @Test
    public void changeInformationTest() throws BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchMethodException,InvocationTargetException,IllegalAccessException {
        User editedUser = userService.changeInformation(1L, editProfileDTO);
        Method method = userService.getClass().getDeclaredMethod("encryptPassword", String.class);
        method.setAccessible(true);

        Assert.assertEquals(editedUser.getFirstName(), editProfileDTO.getFirstName());
        Assert.assertEquals(editedUser.getLastName(), editProfileDTO.getLastName());
        Assert.assertEquals(editedUser.getPassword(), method.invoke(userService,editProfileDTO.getPassword()).toString());
        Assert.assertEquals(editedUser.getEmail(), editProfileDTO.getEmail());
        Assert.assertEquals(editedUser.getPhone(), editProfileDTO.getPhone());
    }

    @Test(expected = ElementNotFoundException.class)
    public  void getFavouritesUserNotFoundTest()throws ElementNotFoundException {
        List<Room> rooms = new LinkedList<>();
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());

        user.setFavourites(rooms);
        Assert.assertEquals(rooms, userService.viewFavouriteRooms(user.getId()));
    }

    @Test
    public  void getFavouritesTest()throws ElementNotFoundException {
        List<Room> rooms = new LinkedList<>();
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());
        rooms.add(new Room());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setFavourites(rooms);
        Assert.assertEquals(rooms, userService.viewFavouriteRooms(user.getId()));
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticationException() throws UnauthorizedException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MockHttpSession session = Mockito.mock(MockHttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);
        session.setAttribute("userId", null);
        UserService.authentication(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticationOk() throws UnauthorizedException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MockHttpSession session = Mockito.mock(MockHttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);
        session.setAttribute("userId", user.getId());
        boolean result = user.getId().equals(UserService.authentication(request));
        Assert.assertTrue(result);
    }
}
