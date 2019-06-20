package com.example.demo.ControllersTests;

import com.example.demo.controllers.UserController;
import com.example.demo.dao.UserRepository;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.net.ssl.SSLEngineResult;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(MockitoJUnitRunner.Silent.class)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private User user;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserController userControllerMock;

    @InjectMocks
    private UserController userController;


    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
        user = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(),"1234",null );
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void signUp() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MockHttpSession session = Mockito.mock(MockHttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getAttribute("userId")).thenReturn(null);


        mockMvc.perform( MockMvcRequestBuilders
                .post("/users")
                .content(mapToJson(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserDetails() throws Exception{
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        mockMvc.perform( MockMvcRequestBuilders
                .get("/users/{userId}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").value(1));
    }

}
