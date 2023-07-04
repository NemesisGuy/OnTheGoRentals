/* BranchServiceImpl.java
 Entity for the Branch
 Author: Asiphe Funda (215092317)
 Date: 19 March 2023

 */
package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Branch;
import za.ac.cput.repository.impl.BranchRepositoryImpl;
import za.ac.cput.service.IBranchService;

import java.util.Set;

@Service
public class BranchServiceImpl implements IBranchService {
    private static BranchServiceImpl service;
    private static BranchRepositoryImpl repository;

    private BranchServiceImpl() {
        repository = BranchRepositoryImpl.getRepository();
    }

    public static BranchServiceImpl getService() {
        if (service == null) {
            service = new BranchServiceImpl();
        }
        return service;
    }

    @Override
    public Branch create(Branch branch) {
        Branch created = repository.create(branch);
        return created;
    }

    @Override
    public Branch read(Integer id) {
        Branch read = repository.read(id);
        return read;
    }

    @Override
    public Branch update(Branch branch) {
        Branch updated = repository.update(branch);
        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = repository.delete(id);
        return success;
    }

    @Override
    public Set<Branch> getAll() {
        return repository.getAll();
    }
}
