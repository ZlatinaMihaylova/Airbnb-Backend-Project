package com.example.demo.model;

import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name="amenities")
@Getter
@Setter
public class Amenity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	private String name;

	@ManyToMany(mappedBy = "amenities",cascade = CascadeType.ALL)
	private Set<Room> rooms;
}
