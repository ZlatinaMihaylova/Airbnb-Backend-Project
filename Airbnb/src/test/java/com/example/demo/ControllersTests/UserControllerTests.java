package com.example.demo.ControllersTests;

import com.example.demo.dto.*;
import com.example.demo.model.Booking;
import com.example.demo.model.City;
import com.example.demo.model.Room;
import com.example.demo.model.User;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoomService roomService;
    @MockBean
    private BookingService bookingService;


    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

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

        mockHttpServletRequest.setSession(session);
        session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
    }

    @Test
    public void signUpShouldReturnBadRequestDueToInvalidBirthday() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SignUpDTO("FirstName", "LastName", "password", "email", LocalDate.now().plusDays(1), "phone"))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void signUpShouldReturnBadRequestDueToEmptyName() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SignUpDTO("", "LastName", "password", "email", LocalDate.now().minusMonths(1), "phone"))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void signUpShouldPass() throws Exception{
        SignUpDTO signUpDTO = new SignUpDTO("FirstName", "LastName", "password", "email", LocalDate.now().minusMonths(1), "phone");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(signUpDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ArgumentCaptor<SignUpDTO> signUpDTOArgumentCaptor = ArgumentCaptor.forClass(SignUpDTO.class);
        Mockito.verify(userService, Mockito.times(1))
                .signUp(signUpDTOArgumentCaptor.capture(), Mockito.eq(session));

        Assert.assertEquals(signUpDTO.getFirstName(), signUpDTOArgumentCaptor.getValue().getFirstName());
        Assert.assertEquals(signUpDTO.getLastName(), signUpDTOArgumentCaptor.getValue().getLastName());
        Assert.assertEquals(signUpDTO.getPassword(), signUpDTOArgumentCaptor.getValue().getPassword());
        Assert.assertEquals(signUpDTO.getEmail(), signUpDTOArgumentCaptor.getValue().getEmail());
        Assert.assertEquals(signUpDTO.getBirthDate(), signUpDTOArgumentCaptor.getValue().getBirthDate());
        Assert.assertEquals(signUpDTO.getPhone(), signUpDTOArgumentCaptor.getValue().getPhone());
    }

    @Test
    public void loginShouldReturnBadRequestDueToEmptyEmail() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginDTO("", "password"))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginShouldPass() throws Exception{
        LoginDTO loginDTO = new LoginDTO("email", "password");

        Mockito.when(userService.login(Mockito.any(LoginDTO.class), Mockito.any(HttpSession.class))).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/userId=" + user.getId()));

        ArgumentCaptor<LoginDTO> loginDTOArgumentCaptor = ArgumentCaptor.forClass(LoginDTO.class);
        Mockito.verify(userService, Mockito.times(1))
                .login(loginDTOArgumentCaptor.capture(), Mockito.eq(session));

        Assert.assertEquals(loginDTO.getEmail(), loginDTOArgumentCaptor.getValue().getEmail());
        Assert.assertEquals(loginDTO.getPassword(), loginDTOArgumentCaptor.getValue().getPassword());
    }

    @Test
    public void logoutShouldPass() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUserDetailsShouldPass() throws Exception{
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.convertUserToDTO(user)).thenReturn(new GetUserProfileDTO(user.viewAllNames(), user.getPhone(), new LinkedList<>(), new LinkedList<>()));

        mvc.perform(MockMvcRequestBuilders.get("/users/userId={userId}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.names", Matchers.is(user.viewAllNames())))
                .andExpect(jsonPath("$.phone", Matchers.is(user.getPhone())));
    }

    @Test
    public void getLoggedUserProfileShouldPass() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/profile")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(redirectedUrl("/users/userId=" + user.getId()));
    }

    @Test
    public void changeInformationShouldPass() throws Exception{
        EditProfileDTO editProfileDTO = new EditProfileDTO("First", "Last", "password", "email", LocalDate.now().minusMonths(1), "phone");
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.put("/changeInformation")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editProfileDTO)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(redirectedUrl("/users/userId=" + user.getId()));

        ArgumentCaptor<EditProfileDTO> editProfileCaptor = ArgumentCaptor.forClass(EditProfileDTO.class);
        Mockito.verify(userService, Mockito.times(1))
                .changeInformation(Mockito.eq(user),  editProfileCaptor.capture());
        Assert.assertEquals(editProfileDTO.getFirstName(), editProfileCaptor.getValue().getFirstName());
        Assert.assertEquals(editProfileDTO.getLastName(), editProfileCaptor.getValue().getLastName());
        Assert.assertEquals(editProfileDTO.getPassword(), editProfileCaptor.getValue().getPassword());
        Assert.assertEquals(editProfileDTO.getEmail(), editProfileCaptor.getValue().getEmail());
        Assert.assertEquals(editProfileDTO.getBirthDate(), editProfileCaptor.getValue().getBirthDate());
        Assert.assertEquals(editProfileDTO.getPhone(), editProfileCaptor.getValue().getPhone());
    }

    @Test
    public void changeInformationShouldReturnBadRequestDueToEmptyParam() throws Exception{
        EditProfileDTO editProfileDTO = new EditProfileDTO("", "Last", "password", "email", LocalDate.now().minusMonths(1), "phone");

        mvc.perform(MockMvcRequestBuilders.put("/changeInformation")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editProfileDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void changeInformationShouldReturnBadRequestDueToFutureBirthDate() throws Exception{
        EditProfileDTO editProfileDTO = new EditProfileDTO("First", "Last", "password", "email", LocalDate.now().plusDays(1), "phone");

        mvc.perform(MockMvcRequestBuilders.put("/changeInformation")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editProfileDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void viewFavouriteRoomsShouldReturnOneRoom() throws Exception{
        Mockito.when(userService.viewFavouriteRooms(user)).thenReturn(new LinkedList<>(Arrays.asList(room)));
        Mockito.when(roomService.convertRoomToDTO(room))
                .thenReturn(new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3));
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/viewFavourites")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))

                .andExpect(jsonPath("$[0].mainPhoto", Matchers.is("1")))
                .andExpect(jsonPath("$[0].name", Matchers.is(room.getName())))
                .andExpect(jsonPath("$[0].city", Matchers.is(room.getCity().getName())))
                .andExpect(jsonPath("$[0].rating", Matchers.is(3.4)))
                .andExpect(jsonPath("$[0].timesRated", Matchers.is(3)));
    }

    @Test
    public void showMyBookingsShouldReturnOnBooking() throws Exception{
        Mockito.when(bookingService.getAllUsersBookings(user)).thenReturn(new LinkedList<>(Arrays.asList(booking)));
        Mockito.when(bookingService.convertBookingToDTO(booking))
                .thenReturn(new GetBookingInfoDTO(user.viewAllNames(), booking.getStartDate(), booking.getEndDate(),
                        new GetListOfRoomDTO("1", room.getName(), city.getName(), 3.4, 3)));
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/myBookings")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
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
