package pl.edu.agh.ii.cinemaProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    private long id;
    private long schedule_id;
    private int seat_nr;
    private LocalDateTime date_of_purchase;
}
