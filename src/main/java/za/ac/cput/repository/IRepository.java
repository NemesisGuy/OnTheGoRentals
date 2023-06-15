package za.ac.cput.repository;

/**
 * IRepository.java
 * interface for the IRepository
 * Author: Peter Buckingham (220165289)
 * Date: 17 March 2021
 */
@Deprecated
public interface IRepository<T, ID> {
    T create(T entity);

    T read(ID id);

    T update(T entity);

    boolean delete(ID id);
}