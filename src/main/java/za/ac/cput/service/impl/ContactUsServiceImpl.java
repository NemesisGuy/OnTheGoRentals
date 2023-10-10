package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.ContactUs;
import za.ac.cput.repository.ContactUsRepository;
import za.ac.cput.service.IContactUsService;

import java.util.ArrayList;

@Service("ContactUsServiceImpl")
public class ContactUsServiceImpl implements IContactUsService {

    private ContactUsRepository repository;

    public ContactUsServiceImpl(ContactUsRepository repository){
        this.repository = repository;
    }
    @Override
    public ContactUs create(ContactUs contactUs) {
        return this.repository.save(contactUs);
    }

    @Override
    public ContactUs read(int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public ContactUs update(ContactUs contactUs) {
        if (this.repository.existsById(contactUs.getId())){
            return this.repository.save(contactUs);
        }
        return null;
    }

    @Override
    public boolean deleteById(int id) {
        if (this.repository.existsById(id)){
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<ContactUs> findAll() {
        ArrayList<ContactUs> all = (ArrayList<ContactUs>) this.repository.findAll();
        return all;
    }


}
