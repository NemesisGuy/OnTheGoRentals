package za.ac.cput.Service;

public interface IService<T> {
    T create(T entity);
    T read(int id);
    T update(T entity);
    void delete(int id);
}