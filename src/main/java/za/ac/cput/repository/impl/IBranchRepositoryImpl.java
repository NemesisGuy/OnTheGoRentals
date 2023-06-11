
/* BranchRepositoryImpl.java
 Entity for the Branch
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository.impl;

import za.ac.cput.domain.impl.Branch;
import za.ac.cput.repository.IBranchRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IBranchRepositoryImpl implements IBranchRepository {
    private List<Branch> branch;

    public IBranchRepositoryImpl() {
        branch = new ArrayList<>();
    }
    @Override
    public Branch create(Branch entity) {
        branch.add(entity);
        return entity;
    }

    @Override
    public Branch read(Integer id) {
        return branch.stream()
                .filter(branch -> branch.getId()==id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Branch update(Branch entity) {
        Branch branchToUpdate = read(entity.getId());

        if (branchToUpdate != null) {
            branch.remove(branchToUpdate);
            branch.add(entity);
            return entity;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {

        Branch branchToDelete = read(id);

        if (branchToDelete != null) {
            branch.remove(branchToDelete);
            return true;
        }

        return false;
    }
    @Override
    public List<Branch> getAll() {
        return Collections.unmodifiableList(branch);
    }
    @Override
    public Branch getBranchById(Integer id) {

        return read(id);
    }
}

