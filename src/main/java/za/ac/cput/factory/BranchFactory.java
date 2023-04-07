package za.ac.cput.factory;

import za.ac.cput.domain.Address;
import za.ac.cput.domain.Branch;

import java.util.List;
import java.util.Random;

import static za.ac.cput.domain.Car.builder;

public class BranchFactory implements IFactory<Branch> {
    @Override
    public Branch create() {
        return Branch.builder()
                .id(new Random().nextInt(1000))
                .build();
    }

    @Override
    public Branch getById(long id) {
        return null;
    }

    @Override
    public Branch update(Branch entity) {
        return null;
    }

    @Override
    public boolean delete(Branch entity) {
        return false;
    }

    @Override
    public List<Branch> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Branch> getType() {
        return Branch.class;
    }
}


