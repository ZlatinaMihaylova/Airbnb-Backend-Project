package com.example.demo.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NonNull
	private String firstName;
	
	@NonNull
	private String lastName;
	
	@NonNull
	private String password;
	
	@NonNull
	private String email;
	
	
	@NonNull
	private LocalDate birthDate;
	
	@NonNull
	private String phone;
	
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name ="favourites",
	joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "room_id"))
	private Set<Room> favourites;
	
	@Override
	public boolean equals(Object obj) {
		return this.email.equals(((User)obj).getEmail());
	}
	
	@Override
	public int hashCode() {
		return this.email.hashCode();
	}
	
	public String viewAllNames() {
		return this.firstName + " " + this.lastName;
	}
	
}
