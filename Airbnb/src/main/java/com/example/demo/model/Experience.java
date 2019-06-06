package com.example.demo.model;


import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String address;

    @NonNull
    private int price;

    @NonNull
    private String details;

    @NonNull
    @ManyToOne
    private City city;

    private Long userId;

    @ManyToMany(mappedBy = "favourites", cascade = CascadeType.ALL)
    private List<User> inFavourites;
}
