package com.example.demo.EntityTests;

import com.example.demo.dao.AmenityRepository;
import com.example.demo.model.Amenity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.Matchers.*;

import java.util.HashSet;
import java.util.List;

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
        System.out.println(amenityRepository.findAll());
        Assert.assertTrue(amenityRepository.findAll().isEmpty());
    }

    @Test
    public void whenFindAll_thenReturnListOfUsers() {
        //given
        Amenity firstUser = new Amenity();
        firstUser.setName("   sakd");
        testEntityManager.persist(firstUser);
        Amenity secondUser = new Amenity();
        secondUser.setName("name");
        testEntityManager.persist(secondUser);
        //when
        List<Amenity> users = amenityRepository.findAll();
        //then
        System.out.println( "*********" + amenityRepository.findAll().size());
        Assert.assertThat(users, hasSize(2));
    }
}
