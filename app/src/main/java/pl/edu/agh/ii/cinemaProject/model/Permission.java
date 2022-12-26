package pl.edu.agh.ii.cinemaProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    public long id;
    public String name;
}
