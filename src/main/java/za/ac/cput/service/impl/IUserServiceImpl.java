package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.User;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IUserService;

import java.util.ArrayList;
import java.util.Collections;
@Service("userServiceImpl")
public class IUserServiceImpl implements IUserService {
    private  IUserRepository repository = null;


    private IUserServiceImpl(IUserRepository repository) {

        this.repository = repository;
    }


    @Override
    public User create(User user) {
        return this.repository.save(user);

    }

    @Override
    public User read(Integer integer) {

        return (User) this.repository.findAllById(Collections.singleton(integer));

    }

    @Override
    public User read(int id) {
        return (User) (User) this.repository.findAllById(Collections.singleton(id));
    }

    @Override
    public User update(User user) {
        if (this.repository.existsById(user.getId()))
            return this.repository.save(user);

        return null;
    }

    @Override
    public boolean delete(int id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean delete(Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<User> getAll() {
        return (ArrayList<User>) this.repository.findAll();
    }


    ///////////


}
