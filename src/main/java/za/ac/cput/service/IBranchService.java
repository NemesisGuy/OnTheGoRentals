package za.ac.cput.service;


import za.ac.cput.domain.impl.Branch;

import java.util.Set;

public interface IBranchService {
    Branch create(Branch branch);
    public Branch read(Integer id);
    Branch update(Branch branch);
    boolean delete(Integer id);
    Set<Branch> getAll();

}
