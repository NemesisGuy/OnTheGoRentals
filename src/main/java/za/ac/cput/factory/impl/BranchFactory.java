/* MaintenanceFactory.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/

package za.ac.cput.factory.impl;


import za.ac.cput.domain.impl.Branch;
import za.ac.cput.factory.IFactory;

import java.util.List;
import java.util.Random;

public class BranchFactory implements IFactory<Branch> {
    @Override
    public Branch create() {
        return Branch.builder()
                .branchId(new Random().nextInt(1000))
                .build();
    }


    public Branch getById(long id) {
        return null;
    }


    public Branch update(Branch entity) {
        return null;
    }


    public boolean delete(Branch entity) {
        return false;
    }

    public List<Branch> getAll() {
        return null;
    }


    public long count() {
        return 0;
    }


    public Class<Branch> getType() {
        return null;
    }


}

