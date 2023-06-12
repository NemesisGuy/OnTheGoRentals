package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Branch;
import za.ac.cput.factory.impl.BranchFactory;


class BranchFactoryTest {
    @Test
    public void test(){
        BranchFactory branchFactory = new BranchFactory();
        Branch branch = branchFactory.create();

        Assertions.assertNotNull(branch);
        Assertions.assertNotNull(branch.getId());
    }

}