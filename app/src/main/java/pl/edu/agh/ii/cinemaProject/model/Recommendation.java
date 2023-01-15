package pl.edu.agh.ii.cinemaProject.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {
    @Id
    private long id;
    private long movie_id;

    public Recommendation(long movie_id) {
        this.movie_id = movie_id;
    }

    public static Recommendation fromMovieId(long movie_id) {
        return new Recommendation(movie_id);
    }
}
