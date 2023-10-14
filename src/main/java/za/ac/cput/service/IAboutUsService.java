package za.ac.cput.service;
/**IAboutUsService.java
 * Interface for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */
import za.ac.cput.domain.AboutUs;

import java.util.List;

public interface IAboutUsService {

    AboutUs create(AboutUs aboutUs);
    AboutUs read(int id);
    AboutUs update(AboutUs aboutUs);
    boolean delete(int id);
    List<AboutUs>getAll();
}

