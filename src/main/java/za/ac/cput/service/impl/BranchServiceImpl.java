/* BranchServiceImpl.java
 Entity for the Branch
 Author: Asiphe Funda (215092317)
 Date: 19 March 2023

 */
package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.Branch;
import za.ac.cput.repository.IBranchRepository;
import za.ac.cput.repository.impl.BranchRepositoryImpl;
import za.ac.cput.service.IBranchService;

import java.util.Set;


public class BranchServiceImpl implements IBranchService {
private static IBranchService service = null;
private IBranchRepository repository =  null;
private BranchServiceImpl(){
    repository = BranchRepositoryImpl.getRepository();
}
public static IBranchService getService(){
    if(service == null){
        service= new BranchServiceImpl();
    }
    return service;
}
    @Override
    public Branch create(Branch branch) {
        Branch created = repository.create(branch);
        return created;
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
