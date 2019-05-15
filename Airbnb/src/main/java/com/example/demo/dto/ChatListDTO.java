package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatListDTO {

	private String userName;
	private String lastMessage;
	private LocalDateTime timeOfLastMessage;

}
