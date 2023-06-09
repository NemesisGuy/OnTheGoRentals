package za.ac.cput.service;

/**
 * IRepository.java
 * interface for the IRepository
 * Author: Peter Buckingham (220165289)
 * Date: 08 June 2023
 */
public interface IService<T, ID> {
    T create(T entity);

    T read(ID id);

    T update(T entity);

    boolean delete(ID id);

}
