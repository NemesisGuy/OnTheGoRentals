package za.ac.cput.domain.dto;

/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import lombok.Data;

@Data
public class BearerToken {

    private String accessToken ;
    private String tokenType ;

    public BearerToken(String accessToken , String tokenType) {
        this.tokenType = tokenType ;
        this.accessToken = accessToken;
    }


}
