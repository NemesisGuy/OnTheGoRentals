package za.ac.cput.domain;
/**
 *
 * Author: Peter Buckingham (220165289)
 * Date: 9 September 2023
 * Started stub for driver
 */
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
@Entity
public class Driver {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //is a users
    @OneToOne //one driver to one user
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "driver") //one driver to many rentals
    private List<Rental> rentals = new ArrayList<>();
}
