package pl.edu.agh.ii.cinemaProject.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private long roleId;

}
