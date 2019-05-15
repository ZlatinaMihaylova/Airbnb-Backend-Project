package com.example.demo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NonNull
	private String address;
	@NonNull
	private int guests;
	@NonNull
	private int bedrooms;
	@NonNull
	private int beds;
	@NonNull
	private int baths;
	@NonNull
	private int price;
	@NonNull
	private String details;
	
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name ="rooms_amenities",
	joinColumns = @JoinColumn(name = "room_id"),
	inverseJoinColumns = @JoinColumn(name = "amenity_id"))
	private Set<Amenity> amenities;
/*	
	@OneToMany(mappedBy = "room")
	private Set<Booking> bookings;
	
	@OneToMany(mappedBy = "room")
	private Set<Photo> photos;
*/
	
	@NonNull
	@ManyToOne
	private City city;
	
	private Long userId;
	
	@ManyToMany(mappedBy = "favourites")
	private Set<User> inFavourites;
	
}
