/* MaintenanceFactory.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/

package za.ac.cput.factory;


import za.ac.cput.domain.Branch;

import java.util.List;
import java.util.Random;

public class BranchFactory implements IFactory<Branch> {
    @Override
    public Branch create() {
        return Branch.builder()
                .branchId(new Random().nextInt(1000))
                .build();
    }

    @Override
    public Branch getById(long id) {
        return null;
    }

    @Override
    public Branch update(Branch entity) {
        return null;
    }

    @Override
    public boolean delete(Branch entity) {
        return false;
    }

    @Override
    public List<Branch> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Branch> getType() {
        return null;
    }


}

