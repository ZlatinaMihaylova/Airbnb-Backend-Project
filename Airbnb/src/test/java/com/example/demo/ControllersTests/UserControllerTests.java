package com.example.demo.ControllersTests;

import com.example.demo.dto.AddPhotoDTO;
import com.example.demo.dto.GetUserProfileDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.SignUpDTO;
import com.example.demo.model.City;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.LinkedList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

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
    public void signUpShouldPast() throws Exception{
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
    public void loginShouldPast() throws Exception{
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
    public void logoutShouldPast() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUserDetailsShouldPast() throws Exception{
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.convertUserToDTO(user)).thenReturn(new GetUserProfileDTO(user.viewAllNames(), user.getPhone(), new LinkedList<>(), new LinkedList<>()));

        mvc.perform(MockMvcRequestBuilders.get("/users/userId={userId}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.names", Matchers.is(user.viewAllNames())))
                .andExpect(jsonPath("$.phone", Matchers.is(user.getPhone()))); 
    }
}
