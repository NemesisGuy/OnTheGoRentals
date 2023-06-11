package za.ac.cput.service;

import za.ac.cput.domain.impl.User;

import java.util.ArrayList;

public interface IUserService extends IService<User, Integer> {
    User create(User user);

    User read(int id);

    User update(User user);

    boolean delete(int id);

    ArrayList<User> getAll();
}