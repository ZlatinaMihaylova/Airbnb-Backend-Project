package com.example.demo.ControllersTests;

import com.example.demo.dto.ChatListDTO;
import com.example.demo.dto.ChatWithUserDTO;
import com.example.demo.dto.GetReviewsForRoomDTO;
import com.example.demo.dto.SendMessageDTO;
import com.example.demo.model.City;
import com.example.demo.model.Message;
import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockHttpServletRequest;
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
public class MessageControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    private User sender;
    private User receiver;
    private Message message;
    private LocalDateTime localDateTime;
    private MockHttpSession session;

    @Before
    public void init() {
        sender = new User(1L, "Sender", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);
        receiver = new User(2L, "Receiver", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);
        localDateTime = LocalDateTime.of(2017,12,12, 12,12, 12);
        message = new Message(1L, sender,receiver, "Text", localDateTime );

        mockHttpServletRequest.setSession(session);
        session = new MockHttpSession();
        session.setAttribute("userId", sender.getId());
    }

    @Test
    public void getAllMessagesShouldReturnOnlyOneMessage() throws Exception {
        Mockito.when(messageService.getAllMessagesForMessagePage(sender))
                .thenReturn(new LinkedList<>(Arrays.asList(new ChatListDTO(receiver.viewAllNames(), message.getText(), localDateTime))));
        Mockito.when(userService.getUserById(sender.getId())).thenReturn(sender);
        mvc.perform(MockMvcRequestBuilders
                .get("/messages")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].userName", Matchers.is(receiver.viewAllNames())))
                .andExpect(jsonPath("$[0].lastMessage", Matchers.is(message.getText())))
                .andExpect(jsonPath("$[0].timeOfLastMessage", Matchers.is(localDateTime.toString())));
    }

    @Test
    public void getMessagesWithUserByIdReturnOnlyOneMessage() throws Exception {
        Mockito.when(messageService.getMessagesWithUser(sender, receiver))
                .thenReturn(new LinkedList<>(Arrays.asList(new ChatWithUserDTO(sender.viewAllNames(), message.getText(), localDateTime))));
        Mockito.when(userService.getUserById(sender.getId())).thenReturn(sender);
        Mockito.when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        mvc.perform(MockMvcRequestBuilders
                .get("/messages/userId={userId}", receiver.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].senderName", Matchers.is(sender.viewAllNames())))
                .andExpect(jsonPath("$[0].text", Matchers.is(message.getText())))
                .andExpect(jsonPath("$[0].time", Matchers.is(localDateTime.toString())));
    }

    @Test
    public void sendMessageShouldReturnBadRequestDueToEmptyMessage() throws Exception {
        SendMessageDTO sendMessageDTO = new SendMessageDTO("");

        mvc.perform(MockMvcRequestBuilders
                .post("/messages/userId={receiverId}", receiver.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendMessageDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sendMessageShouldPassAndRedirect() throws Exception {
        SendMessageDTO sendMessageDTO = new SendMessageDTO("TextMessage");

        mvc.perform(MockMvcRequestBuilders
                .post("/messages/userId={receiverId}", receiver.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendMessageDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/messages/" + receiver.getId()));
    }

}
