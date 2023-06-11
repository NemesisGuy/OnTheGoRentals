package za.ac.cput.repository;

/**
 * IEmployeeRepo.java
 * Class for EmployeeRepoInterface
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 1 April 2023
 */
public interface IEmployeeRepo<T, ID> {
    T create(T Employee);

    T read(ID id);

    T update(T Employee);

    boolean delete(ID id);
}
