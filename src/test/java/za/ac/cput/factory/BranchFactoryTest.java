package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Address;
import za.ac.cput.domain.impl.Branch;



class BranchFactoryTest {
    @Test
            public void test(){
    Branch branch= new Branch.Builder()
            .setBranchId(234)
            .setBranchName("CBD")
            .setAddress(Address.builder().build())
            .setEmail("215092317@mycput.ac.za")
            .build();

        //System.out.println(email.toString());

        Assertions.assertNotNull(branch);
        Assertions.assertEquals("CBD", branch.getBranchName());
        Assertions.assertEquals("215092317@mycput.ac.za", branch.getEmail());
    }

}