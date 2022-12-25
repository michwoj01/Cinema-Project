package pl.edu.agh.ii.cinemaProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    private long id;
    private String name;
    private long duration;
    private String cover_url;

    private String description;


}
