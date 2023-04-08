package za.ac.cput.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {
    private int id = 12354;
    private String branchName = "CptBranch";
    private Address address = null;
    private String email = "215092317@mycput.ac.za";

    @Test
    public void testBranch(){
        Branch branch= new Branch.Builder()
                .setId(id)
                .setBranchName(branchName)
                .setAddress(address)
                .setEmail(email)
                .build();

        System.out.println(email.toString());
    }
    @Test
    public void testObjectIdentity(){
        Branch branch= new Branch.Builder()
                .setId(102030)
                .setBranchName("CBD")
                .setAddress(address)
                .setEmail(email)
                .build();

        Branch branch1= new Branch.Builder()
                .setId(102030)
                .setBranchName("CBD")
                .setAddress(address)
                .setEmail(email)
                .build();

        assertNotSame(branch,branch1);
    }
    @Test
    public void testInequality(){
        Branch branch= new Branch.Builder()
                .setId(123450)
                .setBranchName("CBD")
                .setAddress(address)
                .setEmail(email)
                .build();

        Branch branch1= new Branch.Builder()
                .setId(102030)
                .setBranchName("Capegate car")
                .setAddress(address)
                .setEmail(email)
                .build();

        assertNotEquals(branch,branch);
    }
}