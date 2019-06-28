package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

import com.example.demo.dto.SendMessageDTO;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
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

	public Map<Long, TreeSet<Message>> getUserAllMessages(long userId){
		Map<Long, TreeSet<Message>> userAllMessages = new HashMap<Long, TreeSet<Message>>();
		for (Message message : messageRepository.findAll()) {
			Long otherUserId = null;
			if (message.getSenderId().equals(userId)) {
				otherUserId = message.getReceiverId();
			}
			if (message.getReceiverId().equals(userId)) {
				otherUserId = message.getSenderId();
			}

			if (otherUserId != null ) {
				if ( userAllMessages.containsKey(otherUserId)) {
					userAllMessages.get(otherUserId).add(message);
				} else {
					userAllMessages.put(otherUserId, new TreeSet<Message>((m1,m2) -> m1.getDateTime().compareTo(m2.getDateTime())));
					userAllMessages.get(otherUserId).add(message);
				}
			}
		}
		return userAllMessages;
	}

	public List<ChatListDTO> getAllMessagesForMessagePage(long userId) throws ElementNotFoundException {
		Map<Long, TreeSet<Message>> userAllMessages = new HashMap<Long, TreeSet<Message>>();
		userAllMessages = this.getUserAllMessages(userId);
		List<ChatListDTO> messagesList = new LinkedList<>();
		for (Entry<Long, TreeSet<Message>> entry: userAllMessages.entrySet()) {
			Message message = entry.getValue().last();
			messagesList.add(new ChatListDTO(userService.getUserById(userId).viewAllNames()
					,message.getText(),message.getDateTime()));
		}
		return messagesList;
	}

	public List<ChatWithUserDTO> getMessagesWithUserById(long userId, long otherUserId) throws UnauthorizedException, ElementNotFoundException{
		if ( userId == otherUserId) {
			throw new UnauthorizedException("User don't have messages with himself!");
		}
		List<ChatWithUserDTO> chat = new LinkedList<>();
		Set<Message> messages = new TreeSet<Message>((m1,m2) -> m1.getDateTime().compareTo(m2.getDateTime()));
		messages = this.getUserAllMessages(userId).get(otherUserId);
		if ( messages.isEmpty()) {
			throw new ElementNotFoundException("No messages with this user!");
		}

		for ( Message m : messages) {
			chat.add(new ChatWithUserDTO( userService.getUserById(userId).viewAllNames(),
					m.getText(), m.getDateTime()));
		}
		return chat;
	}

	public void sendMessage(long senderId, long receiverId, SendMessageDTO sendMessageDTO) throws UnauthorizedException, ElementNotFoundException {
		if ( senderId == receiverId) {
			throw new UnauthorizedException("User can not send message to himself!");
		}
		LocalDateTime time = LocalDateTime.now();
		userService.getUserById(senderId);
		userService.getUserById(receiverId);
		Message message = new Message(null,senderId,receiverId, sendMessageDTO.getText(), time);
		messageRepository.saveAndFlush(message);
	}



}
