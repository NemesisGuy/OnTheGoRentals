package za.ac.cput.domain;

public class Branch implements IDomain{
    private int branchId;
    private String branchName;
    private Address address;
    private String email;

    private Branch(Builder builder) {
        this.branchId = builder.branchId;
        this.branchName = builder.branchName;
        this.address = builder.address;
        this.email = builder.email;
    }

    public int getBranchId() {
        return branchId;
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
                "branchId=" + branchId +
                ", branchName='" + branchName + '\'' +
                ", address=" + address +
                ", email=" + email +
                '}';
    }

    @Override
    public int getId() {
        return 0;
    }

    public static class Builder {
        private int branchId;
        private String branchName;
        private Address address;
        private String email;

        public Builder setBranchId(int branchId) {
            this.branchId = branchId;
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
            this.branchId = branch.branchId;
            this.branchName = branch.branchName;
            this.address = branch.address;
            this.email = branch.email;
            return this;
        }

        public Branch build() {
            return new Branch(this);
        }

        public Builder branchId(int nextInt) {
            return null;
        }
    }
}




