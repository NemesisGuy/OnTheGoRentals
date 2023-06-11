/* MaintenanceFactory.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/

package za.ac.cput.factory.impl;


import za.ac.cput.domain.impl.Branch;
import za.ac.cput.factory.IFactory;

import java.util.Random;

public class BranchFactory implements IFactory<Branch> {

    public static Branch buildBranch(String capeGate) {
        return Branch.builder().build();
    }

    @Override
    public Branch create() {
        return new Branch.Builder()
                .setId(new Random().nextInt(1000))
                .setBranchName("Cape Gate")
                .setAddress(null)
                .setEmail("215092317@mycput.ac.za")
                .build();
    }
}

