package com.example.demo.RepoTests;

import com.example.demo.dao.AmenityRepository;
import com.example.demo.model.Amenity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.Matchers.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AmenityRepositoryTests {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    public void shouldFindNoAmenityIfRepoIsEmpty() {
        Assert.assertTrue(amenityRepository.findAll().isEmpty());
    }

    @Test
    public void whenFindAll_thenReturnListOfUsers() {
        Amenity firstAmenity = new Amenity(null, "firstAmenity", new HashSet<>());
        testEntityManager.persist(firstAmenity);
        Amenity secondAmenity = new Amenity(null, "secondAmenity", new HashSet<>());
        testEntityManager.persist(secondAmenity);

        List<Amenity> amenities = amenityRepository.findAll();
        Assert.assertThat(amenities, hasSize(2));
    }

    @Test
    public void shouldFindNoAmenityByID() {
        Assert.assertEquals(Optional.empty(), amenityRepository.findById(1L));
    }

    @Test
    public void shouldFindAmenityByID() {
        Amenity amenity = new Amenity(null, "Amenity", new HashSet<>());
        testEntityManager.persist(amenity);
        Assert.assertEquals(Optional.of(amenity), amenityRepository.findById(amenity.getId()));
    }

    @Test
    public void shouldFindNoAmenityByName() {
        Assert.assertEquals(Optional.empty(), amenityRepository.findByName("Amenity"));
    }

    @Test
    public void shouldFindAmenityByName() {
        Amenity amenity = new Amenity(null, "Amenity", new HashSet<>());
        testEntityManager.persist(amenity);
        Assert.assertEquals(Optional.of(amenity), amenityRepository.findByName("Amenity"));
    }
}
