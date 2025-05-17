package za.ac.cput.service;

/**
 * Author: Peter Buckingham (220165289)
 */
public interface IService<T, ID> {
    T create(T t);

    T read(ID id);

    T update(T t);

    boolean delete(ID id);
}
