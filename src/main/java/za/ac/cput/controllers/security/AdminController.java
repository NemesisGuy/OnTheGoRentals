package za.ac.cput.controllers.security;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {


    //RessourceEndPoint:http://localhost:8080/api/admin/hello
    @GetMapping("/hello")
    public String sayHello ()
    {
        return "Hello" ;
    }


}
