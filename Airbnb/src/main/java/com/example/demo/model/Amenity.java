package com.example.demo.model;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="amenities")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of ={"name"})
public class Amenity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	private String name;

	@ManyToMany(mappedBy = "amenities",cascade = CascadeType.ALL)
	private List<Room> rooms;
}
