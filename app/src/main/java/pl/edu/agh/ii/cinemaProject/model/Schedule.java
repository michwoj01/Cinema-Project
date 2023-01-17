package pl.edu.agh.ii.cinemaProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    private long id;
    private LocalDateTime start_date;
    private long nr_of_seats;
    private long movie_id;
    private long cinema_hall_id;
}
