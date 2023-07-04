/* BranchRepositoryImpl.java
 Entity for the Branch
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository.impl;

import za.ac.cput.domain.impl.Branch;
import za.ac.cput.repository.IBranchRepository;

import java.util.HashSet;
import java.util.Set;

public class BranchRepositoryImpl implements IBranchRepository {
    private static BranchRepositoryImpl repository = null;
    private Set<Branch> branchDB = null;

    private BranchRepositoryImpl() {
        branchDB = new HashSet<Branch>();
    }

    public static BranchRepositoryImpl getRepository() {
        if (repository == null) {
            repository = new BranchRepositoryImpl();
        }
        return repository;
    }

    @Override
    public Branch create(Branch branch) {
        boolean success = branchDB.add(branch);
        if (!success)
            return null;
        return branch;
    }

    @Override
    public Branch read(Integer id) {
        for (Branch b : branchDB) {
            if (b.getId() == (id)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public Branch update(Branch branch) {
        Branch branchOriginal = read(branch.getId());
        if (branchOriginal == null)
            return null;

        boolean successDelete = branchDB.remove(branchOriginal);
        if (!successDelete)
            return null;

        boolean successAdd = branchDB.add(branch);
        if (!successAdd)
            return null;
        return branch;
    }

    @Override
    public boolean delete(Integer id) {
        Branch branchToDelete = read(id);
        if (branchToDelete == null)
            return false;
        branchDB.remove(branchToDelete);
        return true;
    }

    @Override
    public Set<Branch> getAll() {
        return branchDB;
    }

    @Override
    public Branch getBranchById(Integer id) {

        return read(id);
    }
}

