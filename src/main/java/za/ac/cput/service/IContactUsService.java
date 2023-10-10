package za.ac.cput.service;

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
