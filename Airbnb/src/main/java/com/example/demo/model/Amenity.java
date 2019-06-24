package com.example.demo.model;

import java.util.Set;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="amenities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Amenity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	private String name;

	@ManyToMany(mappedBy = "amenities",cascade = CascadeType.ALL)
	private Set<Room> rooms;
}
