package pl.edu.agh.ii.cinemaProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {
    private long id;
    private String name;
    private String message;
}
