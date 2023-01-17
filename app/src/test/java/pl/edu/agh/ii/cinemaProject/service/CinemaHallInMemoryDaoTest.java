package pl.edu.agh.ii.cinemaProject.service;

import org.junit.jupiter.api.Test;
import pl.edu.agh.ii.cinemaProject.db.CinemaHallInMemoryDao;

public class CinemaHallInMemoryDaoTest {
    CinemaHallInMemoryDao cinemaHallInMemoryDao = new CinemaHallInMemoryDao(CinemaHallInMemoryDaoTest.class.getResource("/mockCinemaHall.json"));

    @Test
    public void testLoadingResource() {
        assert cinemaHallInMemoryDao.getCinemaHallById(1).block().getId() == 1;
    }
}
