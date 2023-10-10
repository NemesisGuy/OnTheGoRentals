package za.ac.cput.domain;
/**AboutUs.java
 * Domain Class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
@Entity
public class AboutUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String address;
    private String officeHours;
    private String email;
    private String telephone;
    private String whatsApp;

    protected AboutUs() {
    }

    public AboutUs(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
        this.officeHours = builder.officeHours;
        this.email = builder.email;
        this.telephone = builder.telephone;
        this.whatsApp = builder.whatsApp;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AboutUs aboutUs = (AboutUs) o;
        return id == aboutUs.id && Objects.equals(address, aboutUs.address) && Objects.equals(officeHours, aboutUs.officeHours) && Objects.equals(email, aboutUs.email) && Objects.equals(telephone, aboutUs.telephone) && Objects.equals(whatsApp, aboutUs.whatsApp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, officeHours, email, telephone, whatsApp);
    }

    @Override
    public String toString() {
        return "AboutUs{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", officeHours='" + officeHours + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", whatsApp='" + whatsApp + '\'' +
                '}';
    }

    public static class Builder {

        private int id;
        private String address;
        private String officeHours;
        private String email;
        private String telephone;
        private String whatsApp;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setOfficeHours(String officeHours) {
            this.officeHours = officeHours;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setTelephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Builder setWhatsApp(String whatsApp) {
            this.whatsApp = whatsApp;
            return this;
        }

        public Builder copy(AboutUs aboutUs){
            this.id = aboutUs.id;
            this.address = aboutUs.address;
            this.officeHours = aboutUs.officeHours;
            this.email = aboutUs.email;
            this.telephone = aboutUs.telephone;
            this.whatsApp = aboutUs.whatsApp;
            return this;
        }

        public AboutUs build() {
            return new AboutUs(this);
        }
    }
}
