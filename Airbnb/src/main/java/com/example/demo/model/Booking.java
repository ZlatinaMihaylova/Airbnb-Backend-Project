package com.example.demo.model;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate startDate;
	private LocalDate endDate;

	@NonNull
	@ManyToOne
	private User user;

	@NonNull
	@ManyToOne
	private Room room;


	public boolean overlap(LocalDate startDate, LocalDate endDate) {
		return !(this.getStartDate().isAfter(endDate) || this.getEndDate().isEqual(startDate) || this.getEndDate().isBefore(startDate));
	}

}
