package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.User;
import za.ac.cput.repository.IRepository;
import za.ac.cput.repository.impl.IUserRepositoryImpl;
import za.ac.cput.service.IUserService;

import java.util.ArrayList;

public class IUserServiceImpl implements IUserService {
    private static IUserServiceImpl service = null;
    private static IUserRepositoryImpl repository = null;
    private ArrayList<User> users;

    public IUserServiceImpl() {
        repository = IUserRepositoryImpl.getRepository();
    }

    public IUserServiceImpl(IUserRepositoryImpl repository) {
        this.repository = repository;
    }

    public IUserServiceImpl(IRepository userRepository) {
        this.repository = repository;
    }

    public static IUserRepositoryImpl getRepository() {
        if (repository == null) {
            repository = IUserRepositoryImpl.getRepository();
        }
        return repository;
    }

    public static IUserServiceImpl getService() {
        if (service == null) {
            service = new IUserServiceImpl();
        }
        return service;
    }

    @Override
    public User create(User user) {
        return repository.create(user);
    }

    @Override
    public User read(Integer integer) {
        return repository.read(integer);
    }

    @Override
    public User read(int id) {
        return repository.read(id);
    }

    @Override
    public User update(User user) {
        return repository.update(user);
    }

    @Override
    public boolean delete(int id) {
        return repository.delete(id);
    }

    public boolean delete(Integer id) {
        return repository.delete(id);
    }

    @Override
    public ArrayList<User> getAll() {
        return (ArrayList<User>) repository.getAllUsers();
    }


    ///////////


}
