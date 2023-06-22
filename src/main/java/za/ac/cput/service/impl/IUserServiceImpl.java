package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.User;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IUserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

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
      //  Optional<Car> optionalCar = this.repository.findById(id);
        Optional <User> optionalUser = this.repository.findById(integer);
        return optionalUser.orElse(null);


    }

    @Override
    public User read(int id) {
        Optional<User> optionalUser = this.repository.findById(id);
        return optionalUser.orElse(null);
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
