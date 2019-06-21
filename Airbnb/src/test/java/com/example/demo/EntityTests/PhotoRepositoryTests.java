package com.example.demo.EntityTests;

import com.example.demo.dao.AmenityRepository;
import com.example.demo.dao.PhotoRepository;
import com.example.demo.model.Amenity;
import com.example.demo.model.Photo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PhotoRepositoryTests {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    public void shouldFindNoAmenityIfRepoIsEmpty() {
        Assert.assertTrue(photoRepository.findAll().isEmpty());
    }

}
