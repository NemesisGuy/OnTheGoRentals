package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Branch;
import za.ac.cput.factory.impl.BranchFactory;
import za.ac.cput.service.IBranchService;

import java.util.Set;


@RestController
@RequestMapping("/branch")
public class BranchController {
    @Autowired
    private IBranchService branchService;

    @PostMapping("/create")
    public Branch create(@RequestBody Branch branch) {
        //Branch branchCreated = BranchFactory.createBranch(branch.getBranchName());
        Branch newBranch = BranchFactory.createBranch(branch.getBranchName());
        //"CapeGate Branch",null,"215092317@mycput.ac.za");
        return branchService.create(newBranch);
        //return branchService.create(branchCreated);

    }

    @GetMapping("/read/{id}")
    public Branch read(@PathVariable Integer id) {
        return branchService.read(id);
    }

    @PostMapping("/update/")
    public Branch update(@RequestBody Branch branch) {
        return branchService.update(branch);
    }

    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable Integer id) {
        return branchService.delete(id);
    }

    @RequestMapping({"/getall"})
    public Set<Branch> getall() {
        return branchService.getAll();
    }

}
