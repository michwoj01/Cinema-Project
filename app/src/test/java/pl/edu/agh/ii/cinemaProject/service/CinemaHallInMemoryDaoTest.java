package pl.edu.agh.ii.cinemaProject.service;

import org.junit.jupiter.api.Test;
import pl.edu.agh.ii.cinemaProject.db.CinemaHallInMemoryDao;
import java.io.IOException;

public class CinemaHallInMemoryDaoTest {
    CinemaHallInMemoryDao cinemaHallInMemoryDao = new CinemaHallInMemoryDao(CinemaHallInMemoryDaoTest.class.getResource("/mockCinemaHall.json"));


    @Test
    public void testLoadingResource() throws IOException {
        assert cinemaHallInMemoryDao.getCinemaHallById(1).block().getId() == 1;
    }


}
