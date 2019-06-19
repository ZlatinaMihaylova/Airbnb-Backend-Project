package com.example.demo.model;

		import java.time.LocalDate;
		import java.time.LocalDateTime;
		import java.util.Set;

		import javax.persistence.Entity;
		import javax.persistence.GeneratedValue;
		import javax.persistence.GenerationType;
		import javax.persistence.Id;
		import javax.persistence.ManyToOne;
		import javax.persistence.OneToMany;
		import javax.persistence.Table;

		import lombok.*;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Message implements Comparable<Message> {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	private Long senderId;

	@NonNull
	private Long receiverId;

	@NonNull
	private String text;

	@NonNull
	private LocalDateTime dateTime;

	@Override
	public int compareTo(Message message) {
		return this.getDateTime().compareTo(message.getDateTime());
	}


}
