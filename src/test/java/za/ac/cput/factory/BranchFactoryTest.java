package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Branch;


import static org.junit.jupiter.api.Assertions.*;

class BranchFactoryTest {
    @Test
    void testBranchFactory_pass(){
        BranchFactory branchFactory = new BranchFactory();
        Branch branch = branchFactory.create();

        Assertions.assertNotNull(branch);
        Assertions.assertNotNull(branch.getId());

    }
    @Test
    void testBranchFactory_fail(){
        BranchFactory branchFactory = new BranchFactory();
        Branch branch = branchFactory.create();

        Assertions.assertNull(branch);
        Assertions.assertNull(branch.getId());
    }
}