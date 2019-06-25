package com.example.demo.model;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

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
	private String name;
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
	private List<Amenity> amenities;

	@NonNull
	@ManyToOne
	private City city;

	private Long userId;

	@ManyToMany(mappedBy = "favourites", cascade = CascadeType.ALL)
	private List<User> inFavourites;
	
}
