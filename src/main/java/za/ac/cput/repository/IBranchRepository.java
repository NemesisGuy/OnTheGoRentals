/* IBranchRepository.java
 Entity for the Branch
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository;

import za.ac.cput.domain.Branch;

import java.util.Set;

public interface IBranchRepository extends IRepository<Branch, Integer> {

    public Set<Branch> getAll();

    Branch getBranchById(Integer id);

}
