package com.example.demo.ServicesTests;

import com.example.demo.dao.MessageRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.*;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import org.assertj.core.util.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class MessageServiceTests {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private UserService userService;

    @Mock
    private MessageService messageServiceMock;

    @InjectMocks
    private MessageService messageService;

    private User user1;
    private User user2;
    private List<Message> messages;

    @Before
    public void init() {
        user1 = new User(1L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);
        user2 = new User(2L, "FirstName", "LastName", "goodPassword1234", "email@gmail.com", LocalDate.now(), "1234", null);

        messages = new LinkedList<>(Arrays.asList(
                new Message(1L, user1, user2, "Text1", LocalDateTime.of(2017, 2, 13, 15, 56)),
                new Message(2L, user2, user1, "Text2", LocalDateTime.of(2017, 2, 14, 15, 56)),
                new Message(3L, user2, user1, "Text3", LocalDateTime.of(2017, 2, 15, 15, 56)),
                new Message(4L, user1, user2, "Text4", LocalDateTime.of(2017, 2, 16, 15, 56))));
    }

    @Test
    public void getUserAllMessages() {
        Mockito.when(messageRepository.findAll()).thenReturn(messages);
        Map<User, TreeSet<Message>> expected = new HashMap<>();
        expected.put(user2,new TreeSet<>(Sets.newTreeSet(messages.get(0),messages.get(1),messages.get(2),messages.get(3))));
        Assert.assertEquals(expected, messageService.getUserAllMessages(user1));
    }

    @Test
    public void getAllMessagesForMessagePage() throws ElementNotFoundException {
        Mockito.when(messageRepository.findAll()).thenReturn(messages);
        Mockito.when(userServiceMock.getUserById(user1.getId())).thenReturn(user1);
        List<ChatListDTO> expected = new LinkedList<>();
        expected.add(new ChatListDTO(user2.viewAllNames(), "Text4", LocalDateTime.of(2017, 2, 16, 15, 56) ));
        List<ChatListDTO> result = messageService.getAllMessagesForMessagePage(user1);

        Assert.assertEquals(expected, messageService.getAllMessagesForMessagePage(user1));
    }

    @Test
    public void getMessagesWithUserById() throws UnauthorizedException, ElementNotFoundException {
        Mockito.when(messageRepository.findAll()).thenReturn(messages);
        List<ChatWithUserDTO> expected = new LinkedList<>();
        expected.addAll(Arrays.asList(
                new ChatWithUserDTO(user1.viewAllNames(), "Text1", LocalDateTime.of(2017, 2, 13, 15, 56)),
                new ChatWithUserDTO(user2.viewAllNames(), "Text2", LocalDateTime.of(2017, 2, 14, 15, 56)),
                new ChatWithUserDTO(user2.viewAllNames(), "Text3", LocalDateTime.of(2017, 2, 15, 15, 56)),
                new ChatWithUserDTO(user1.viewAllNames(), "Text4", LocalDateTime.of(2017, 2, 16, 15, 56))));
        List<ChatWithUserDTO> result = messageService.getMessagesWithUser(user1, user2);

        Assert.assertEquals(expected, result);
    }

    @Test(expected = UnauthorizedException.class)
    public void getMessagesWithUserByIdShouldReturnUnauthorizedException() throws UnauthorizedException, ElementNotFoundException{
        messageService.getMessagesWithUser(user1, user1);
    }

    @Test(expected = ElementNotFoundException.class)
    public void getMessagesWithUserShouldReturnElementNotFoundException() throws UnauthorizedException, ElementNotFoundException{
        Map<User, TreeSet<Message>> userAllMessages = new HashMap<User, TreeSet<Message>>();
        Mockito.when(messageServiceMock.getUserAllMessages(user1)).thenReturn(userAllMessages);
        messageService.getMessagesWithUser(user1, user2);
    }

    @Test
    public void sendMessage() throws UnauthorizedException, ElementNotFoundException {
        Mockito.when(userServiceMock.getUserById(user1.getId())).thenReturn(user1);
        Mockito.when(userServiceMock.getUserById(user2.getId())).thenReturn(user2);
        Message message = new Message(1L, user1, user2, "Text", LocalDateTime.of(2017, 2, 13, 15, 56));

        messageService.sendMessage(user1, user2, new SendMessageDTO("Text"));

        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(messageRepository).saveAndFlush(argument.capture());

        Assert.assertEquals(message.getSender(), argument.getValue().getSender());
        Assert.assertEquals(message.getReceiver(), argument.getValue().getReceiver());
        Assert.assertEquals(message.getText(), argument.getValue().getText());

    }

    @Test(expected = UnauthorizedException.class)
    public void sendMessageShouldReturnUnauthorizedException() throws UnauthorizedException, ElementNotFoundException {
        messageService.sendMessage(user1, user1, new SendMessageDTO());

    }
}
