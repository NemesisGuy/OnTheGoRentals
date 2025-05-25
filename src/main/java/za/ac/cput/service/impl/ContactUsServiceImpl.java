package za.ac.cput.service.impl;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.repository.ContactUsRepository;
import za.ac.cput.service.IContactUsService;

import java.util.ArrayList;
import java.util.UUID;

@Service("ContactUsServiceImpl")
public class ContactUsServiceImpl implements IContactUsService {

    private final ContactUsRepository repository;

    public ContactUsServiceImpl(ContactUsRepository repository) {
        this.repository = repository;
    }

    @Override
    public ContactUs create(ContactUs contactUs) {
        return this.repository.save(contactUs);
    }

    @Override
    public ContactUs read(int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public ContactUs read(UUID uuid) {
        return this.repository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    @Override
    public ContactUs update(ContactUs contactUs) {
        if (this.repository.existsById(contactUs.getId())) {
            return this.repository.save(contactUs);
        }
        return null;
    }

    @Override
    public boolean delete(int id) {
        ContactUs contactUs = this.repository.findByIdAndDeletedFalse(id).orElse(null);
        if (contactUs != null && !contactUs.isDeleted()) {
            contactUs = new ContactUs.Builder().copy(contactUs).setDeleted(true).build();
            repository.save(contactUs);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<ContactUs> getAll() {
        ArrayList<ContactUs> all = (ArrayList<ContactUs>) this.repository.findByDeletedFalse();
        return all;
    }


}
