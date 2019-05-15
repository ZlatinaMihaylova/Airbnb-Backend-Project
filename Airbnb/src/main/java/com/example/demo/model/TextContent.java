package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class TextContent {

	private User author;
	private String content;
	private LocalDateTime time;
	
	TextContent (String content, User sender,LocalDateTime localDateTime) {
		this.setContent(content);
		this.setAuthor(sender);
		this.time = localDateTime;
	}
}
