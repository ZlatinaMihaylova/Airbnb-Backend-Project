package com.example.demo;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.EditProfileDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.model.User;
import com.example.demo.service.ReviewService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

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

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomService roomServiceMock;

    @InjectMocks
    private ReviewService reviewService;

    private User user;

    @Before
    public void init() {
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
    }

    @Test(expected = SignUpException.class)
    public void testInvalidPassword() throws SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
        userService.signUp(new User(null, "FirstName", "LastName", "1234", "email@gmail.com", LocalDate.now(),"1234",null ));
    }

    @Test(expected = SignUpException.class)
    public void testInvalidEmail() throws SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
        userService.signUp(new User(null, "FirstName", "LastName", "goodPassword1234", "email", LocalDate.now(),"1234",null ));
    }

    @Test(expected = SignUpException.class)
    public void testAlreadyUsedEmail() throws SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));
        userService.signUp(new User(null, "FirstName", "LastName", "goodPassword1234", "email", LocalDate.now(),"1234",null ));
    }

    @Test
    public void testSignUpOK() throws SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException  {
         userService.signUp(user);
    }

    @Test(expected = ElementNotFoundException.class)
    public void getUserByIdNotFound() throws ElementNotFoundException  {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Mockito.when(roomService.getUserRooms(user.getId())).thenReturn((new LinkedList<>()));
        Mockito.when(roomService.getUserReviews(user.getId())).thenReturn((new LinkedList<>()));
        userService.getUserById(user.getId());
    }

    @Test
    public void getUserById() throws ElementNotFoundException {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(roomService.getUserRooms(user.getId())).thenReturn(new LinkedList<>());
        Mockito.when(roomService.getUserReviews(user.getId())).thenReturn(new LinkedList<>());
        UserProfileDTO userProfileDTO = new UserProfileDTO(user.viewAllNames(), user.getPhone(), roomService.getUserRooms(user.getId()), roomService.getUserReviews(user.getId()));
        Assert.assertEquals(userProfileDTO, userService.getUserById(user.getId()));
    }

    @Test(expected = ElementNotFoundException.class)
    public void loginUserNotFound() throws ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        userService.login(new LoginDTO("email@abv.bg", "12345" ));
    }

    @Test
    public void loginSuccessful() throws ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = userService.getClass().getDeclaredMethod("encryptPassword", String.class);
        method.setAccessible(true);
        Mockito.when(userRepository.findByEmailAndPassword(user.getEmail(),method.invoke(userService,user.getPassword()).toString())).thenReturn(Optional.of(user));
        Assert.assertEquals(user,userService.login(new LoginDTO(user.getEmail(),user.getPassword())));
    }



}
