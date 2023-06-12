package za.ac.cput.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.impl.Branch;
import za.ac.cput.factory.impl.BranchFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
class BranchServiceImplTest {
    private static BranchServiceImpl service = BranchServiceImpl.getService();
    private static BranchFactory branchFactory = new BranchFactory();
    private static Branch branch = branchFactory.createBranch("Cape Gate", null, "215092317@mycput.ac.za");

    @Test
    void a_create() {
        Branch created = service.create(branch);
        System.out.println("Created: " + created);
        assertNotNull(created);
    }

    @Test
    void b_read() {
        Branch read = service.read(branch.getId());
        System.out.println("Read: " + read);
        Assertions.assertNotNull(read);
    }

    @Test
    void c_update() {
        Branch updated = new Branch.Builder()
                .copy(branch)
                .setBranchName("(updated)")
                .setEmail("(updated)")
                .build();
        System.out.println("Updated: " + service.update(updated));
        Assertions.assertNotSame(updated, branch);
    }

    @Test
    void d_getAll() {
        System.out.println("Show all: ");
        System.out.println(service.getAll());
    }

    @Test
    void e_delete() {
        Integer id = branch.getId();
        boolean success = service.delete(id);
        assertTrue(success);
        System.out.println(success);
    }
}