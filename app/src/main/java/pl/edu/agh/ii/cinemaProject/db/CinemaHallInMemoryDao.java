package pl.edu.agh.ii.cinemaProject.db;

import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.model.CinemaHall;
import pl.edu.agh.ii.cinemaProject.util.JsonLoader;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CinemaHallInMemoryDao {

    private final Map<Long, CinemaHall> cinemaHalls;
    private final CinemaHall defaultCinemaHall = new CinemaHall(0, 0, 0);

    public CinemaHallInMemoryDao(URL resourceName) {
        var listType = new TypeToken<ArrayList<CinemaHall>>() {
        };

        List<CinemaHall> aa = JsonLoader.loadJsonResource(resourceName, listType).mapLeft((thr) -> {
            System.err.println(thr);
            return thr;
        }).get();
        this.cinemaHalls = aa.stream().collect(Collectors.toMap(CinemaHall::getId, Function.identity()));
    }

    public CinemaHallInMemoryDao() {
        this(CinemaHallInMemoryDao.class.getResource("/cinemaHalls.json"));
    }

    public Mono<CinemaHall> getCinemaHallById(long cinemaHallId) {
        return Mono.just(this.cinemaHalls.getOrDefault(cinemaHallId, defaultCinemaHall));
    }


}
