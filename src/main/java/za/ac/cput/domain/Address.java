/* Address.java
 Entity for the Address
 Author: Asiphe Funda (215092317)
 Date: 19 March 2023
*/

package za.ac.cput.domain;

public class Address implements IDomain{
    private static String streetNumber;
    private static String streetName;
    private static String suburb;
    private static String city;
    private static String state;
    private static int postcode;

    private Address(Builder builder) {
        this.streetNumber = builder.streetNumber;
        this.streetName = builder.streetName;
        this.suburb = builder.suburb;
        this.city = builder.city;
        this.state = builder.state;
        this.postcode = builder.postcode;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getPostcode() {
        return postcode;
    }
    public static Address.Builder builder() {
        return new Address.Builder();
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetNumber='" + streetNumber + '\'' +
                ", streetName='" + streetName + '\'' +
                ", suburb='" + suburb + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postcode=" + postcode +
                '}';
    }

    @Override
    public int getId() {
        return 0;
    }

    public static class Builder{
        private String streetNumber;
        private String streetName;
        private String suburb;
        private String city;
        private String state;
        private int postcode;

        public Builder setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
            return this;
        }

        public Builder setStreetName(String streetName) {
            this.streetName = streetName;
            return this;
        }

        public Builder setSuburb(String suburb) {
            this.suburb = suburb;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        public Builder setPostcode(int postcode) {
            this.postcode = postcode;
            return this;
        }
        public Builder copy (Address address){
            this.streetNumber = Address.streetNumber;
            this.streetName = Address.streetName;
            this.suburb = Address.suburb;
            this.city = Address.city;
            this.state = Address.state;
            this.postcode = Address.postcode;
            return this;
        }
        public Address build() {
            return new Address(this);
        }
    }
}
