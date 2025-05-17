package za.ac.cput.domain.security;
/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    RoleName roleName;

    public Role (String roleName) {
        this.roleName = RoleName.valueOf(roleName);
    }
    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName.toString();
    }
}
