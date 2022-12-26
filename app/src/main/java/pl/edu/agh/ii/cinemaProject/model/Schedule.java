package pl.edu.agh.ii.cinemaProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    private long id;
    private Date start_date;
    private long currently_available;
    private long movie_id;
    private long cinema_hall_id;
}
