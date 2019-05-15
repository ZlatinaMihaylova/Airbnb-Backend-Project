package com.example.demo.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	
	
	public boolean overlap(Booking b) {
		if (b != null) {
			return (this.getStartDate().isAfter(b.getStartDate()) && this.getStartDate().isBefore(b.getEndDate()))
					|| (this.getEndDate().isAfter(b.getStartDate()) && this.getEndDate().isBefore(b.getEndDate()))
					|| (this.getStartDate().isBefore(b.getStartDate())
							&& this.getEndDate().isAfter(b.getStartDate()))
					|| (this.getStartDate().isBefore(b.getEndDate()) && this.getEndDate().isAfter(b.getEndDate()))
					|| (this.getStartDate().isEqual(b.getStartDate())
							|| (this.getStartDate().isEqual(b.getEndDate()))
							|| (this.getEndDate().isEqual(b.getStartDate()))
							|| (this.getEndDate().isEqual(b.getEndDate())));
		}
		return true;
	}
}
