package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

import com.example.demo.dto.SendMessageDTO;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.MessageRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.ChatListDTO;
import com.example.demo.dto.ChatWithUserDTO;
import com.example.demo.model.Message;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserService userService;

	public Map<User, TreeSet<Message>> getUserAllMessages(User user){
		Map<User, TreeSet<Message>> userAllMessages = new HashMap<User, TreeSet<Message>>();

		for (Message message : messageRepository.findAll()) {
			User otherUser = null;
			if (message.getSender().equals(user)) {
				otherUser = message.getReceiver();
			}
			if (message.getReceiver().equals(user)) {
				otherUser = message.getSender();
			}

			if (otherUser != null ) {
				if ( userAllMessages.containsKey(otherUser)) {
					userAllMessages.get(otherUser).add(message);
				} else {
					userAllMessages.put(otherUser, new TreeSet<Message>((m1,m2) -> m1.getDateTime().compareTo(m2.getDateTime())));
					userAllMessages.get(otherUser).add(message);
				}
			}
		}
		return userAllMessages;
	}

	public List<ChatListDTO> getAllMessagesForMessagePage(User user) {
		Map<User, TreeSet<Message>> userAllMessages = new HashMap<User, TreeSet<Message>>();
		userAllMessages = this.getUserAllMessages(user);
		List<ChatListDTO> messagesList = new LinkedList<>();
		for (Entry<User, TreeSet<Message>> entry: userAllMessages.entrySet()) {
			Message message = entry.getValue().last();
			messagesList.add(new ChatListDTO(user.viewAllNames()
					,message.getText(),message.getDateTime()));
		}
		return messagesList;
	}

	public List<ChatWithUserDTO> getMessagesWithUser(User user, User otherUser) throws UnauthorizedException, ElementNotFoundException{
		if ( user.getId() == otherUser.getId()) {
			throw new UnauthorizedException("User don't have messages with himself!");
		}
		List<ChatWithUserDTO> chat = new LinkedList<>();
		Set<Message> messages =  getUserAllMessages(user).get(otherUser);
		if ( messages == null) {
			throw new ElementNotFoundException("No messages with this user!");
		}

		for ( Message m : messages) {
			chat.add(new ChatWithUserDTO( user.viewAllNames(),
					m.getText(), m.getDateTime()));
		}
		return chat;
	}

	public void sendMessage(User sender, User receiver, SendMessageDTO sendMessageDTO) throws UnauthorizedException {
		if ( sender.getId() == receiver.getId()) {
			throw new UnauthorizedException("User can not send message to himself!");
		}
		LocalDateTime time = LocalDateTime.now();
		Message message = new Message(null,sender, receiver, sendMessageDTO.getText(), time);
		messageRepository.saveAndFlush(message);
	}



}
