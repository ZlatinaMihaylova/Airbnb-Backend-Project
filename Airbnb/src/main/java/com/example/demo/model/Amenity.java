package com.example.demo.model;

import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="amenities")
@Getter
@Setter
public class Amenity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;

	@ManyToMany(mappedBy = "amenities",cascade = CascadeType.ALL)
	private Set<Room> rooms;
}
