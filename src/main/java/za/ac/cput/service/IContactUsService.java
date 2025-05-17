package za.ac.cput.service;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import za.ac.cput.domain.ContactUs;

import java.util.ArrayList;
import java.util.List;

public interface IContactUsService {

    ContactUs create(ContactUs contactUs);

    ContactUs read(int id);

    ContactUs update(ContactUs contactUs);

    boolean deleteById(int id);

    ArrayList<ContactUs> findAll();
}
