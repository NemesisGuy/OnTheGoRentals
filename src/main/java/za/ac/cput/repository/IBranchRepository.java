package za.ac.cput.repository;

import za.ac.cput.domain.Branch;
import za.ac.cput.domain.Maintenance;

import java.util.List;

public interface IBranchRepository extends IRepository<Branch, Integer>{

    public List<Branch> getAll();
    Branch getBranchById(Integer id);

}
