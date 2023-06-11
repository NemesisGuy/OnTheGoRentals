package za.ac.cput.domain.impl;

public class Branch {

    private int id;

    private String branchName;
    private Address address;
    private String email;

    private Branch(Builder builder) {
        this.id = builder.id;
        this.branchName = builder.branchName;
        this.address = builder.address;
        this.email = builder.email;
    }

    public int getId() {
        return id;
    }

    public String getBranchName() {
        return branchName;
    }

    public Address getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public static Branch.Builder builder() {
        return new Branch.Builder();
    }
    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", branchName='" + branchName + '\'' +
                ", address=" + address +
                ", email=" + email +
                '}';
    }

    public static class Builder {
        private int id;
        private String branchName;
        private Address address;
        private String email;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setBranchName(String branchName) {
            this.branchName = branchName;
            return this;
        }

        public Builder setAddress(Address address) {
            this.address = address;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder copy(Branch branch) {
            this.id = branch.id;
            this.branchName = branch.branchName;
            this.address = branch.address;
            this.email = branch.email;
            return this;
        }

        public Branch build() {
            return new Branch(this);
        }

        public Builder id(int nextInt) {
            return null;
        }
    }
}




