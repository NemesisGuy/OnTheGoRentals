/* IBranchRepository.java
 Entity for the Branch
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository;

import za.ac.cput.domain.impl.Branch;

import java.util.List;

public interface IBranchRepository extends IRepository<Branch, Integer> {

    public List<Branch> getAll();

    Branch getBranchById(Integer id);

}
