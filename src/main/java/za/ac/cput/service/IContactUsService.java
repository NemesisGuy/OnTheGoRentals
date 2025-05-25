package za.ac.cput.service;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import za.ac.cput.domain.entity.ContactUs;

import java.util.ArrayList;
import java.util.UUID;

public interface IContactUsService {

    ContactUs create(ContactUs contactUs);

    ContactUs read(int id);
    ContactUs read(UUID uuid);

    ContactUs update(ContactUs contactUs);

    boolean delete(int id);

    ArrayList<ContactUs> getAll();


}
