package za.ac.cput.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.impl.Branch;
import za.ac.cput.factory.impl.BranchFactory;
import za.ac.cput.service.impl.BranchServiceImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
class BranchServiceImplTest {
private static IBranchService service = null;
private static Branch branch = BranchFactory.buildBranch("Cape Gate");
public BranchServiceImplTest(){
    IBranchService servive = BranchServiceImpl.getService();

}
    @Test
    void a_create() {
    Branch created = service.create(branch);
    System.out.println(created);
    assertNotNull(created);
    }

    @Test
    void b_delete() {
    Integer id = branch.getId();
    boolean success = service.delete(id);
    assertTrue(success);
    System.out.println("Success" + success);
    }

    @Test
    void c_getAll() {
    System.out.println("Show all: ");
    System.out.println(service.getAll());
    }
}