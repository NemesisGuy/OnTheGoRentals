package za.ac.cput.factory;

import za.ac.cput.domain.impl.Rental;

import java.util.List;

public interface IFactoryRental<R> extends IFactory<Rental> {
    abstract Rental create();

    Rental getById(long id);

    Rental update(Rental entity);

    boolean delete(Rental entity);

    List<Rental> getAll();

    long count();

    Class<Rental> getType();
}
