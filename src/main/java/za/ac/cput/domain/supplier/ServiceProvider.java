package za.ac.cput.domain.supplier;

import java.util.List;
import java.util.Map;

public class ServiceProvider {

    private int id;
    private String name;
    private String type;
    private String description;

    private String contactNumber;
    private String email;
    private String address;

    private boolean isActive;
    private Map<String, String> operatingHours;

    private List<String> servicesProvided;
    private List<String> specializations;

    // Ratings and Reviews
    private double averageRating;
    // private List<String> reviews; //optional can remove if not needed

    // Additional Details
    private String contactPerson;
    private String logoUrl;
    //private List<String> tags; //optional can remove if not needed
    private String registrationNumber;

    public ServiceProvider() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return isActive;
    }

    public Map<String, String> getOperatingHours() {
        return operatingHours;
    }

    public List<String> getServicesProvided() {
        return servicesProvided;
    }

    public List<String> getSpecializations() {
        return specializations;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", isActive=" + isActive +
                ", operatingHours=" + operatingHours +
                ", servicesProvided=" + servicesProvided +
                ", specializations=" + specializations +
                ", averageRating=" + averageRating +
                ", contactPerson='" + contactPerson + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                '}';
    }
}
