package za.ac.cput.domain;



public interface IBranch extends IDomain {

    int getBranchId();


    String getBranchName();



    Address getAddress();



    String getEmail();


}
