package za.ac.cput.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Branch;
import za.ac.cput.factory.impl.BranchFactory;
import za.ac.cput.repository.impl.BranchRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class BranchRepositoryImplTest {
    private static BranchRepositoryImpl repository = BranchRepositoryImpl.getRepository();
    private static BranchFactory repositoryFactory = new BranchFactory();
    private static Branch branch = repositoryFactory.create();
    private static Branch branch2;

    @Test
    void a_create() {
        Branch created = repository.create(branch);
        assertEquals(branch.getId(), created.getId());
        System.out.println("Create: " + created);
    }

    @Test
    void b_read() {
        Branch read = repository.read(branch.getId());
        assertNotNull(read);
        System.out.println("Read: " + read);
    }

    @Test
    void c_update() {

        Branch updated = new Branch.Builder().copy(branch)
                .setId(215092317)
                .setBranchName("Asiphe")
                .setAddress(null)
                .setEmail("215092317@mycput.ac.za")
                .build();
        Assertions.assertNull(repository.update(updated));
        System.out.println("Updated: " + updated);
    }

    @Test
    void d_delete() {
        Integer id = branch.getId();
        boolean success = repository.delete(id);
        assertFalse(success);
        System.out.println("Success: " + success);
    }

    //@Test
    //void e_getAll() {
    // System.out.println("Show all: ");
    //System.out.println(getAll);
    //}
}