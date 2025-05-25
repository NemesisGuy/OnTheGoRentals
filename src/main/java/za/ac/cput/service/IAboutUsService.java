package za.ac.cput.service;
/**
 * IAboutUsService.java
 * Interface for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 */

import za.ac.cput.domain.entity.AboutUs;

import java.util.List;
import java.util.UUID;

public interface IAboutUsService {

    AboutUs create(AboutUs aboutUs);

    AboutUs read(int id);

    AboutUs read(UUID uuid);

    AboutUs update(AboutUs aboutUs);

    boolean delete(int id);

    List<AboutUs> getAll();
}

