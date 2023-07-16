/* Maintenance.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 19 March 2023
*/
package za.ac.cput.domain;

import java.time.LocalDate;

public class Maintenance  {
    private int id;
    private String maintenanceType;
    private String serviceProvider;
    private LocalDate serviceDate;


    private Maintenance(Builder builder) {
        this.id = builder.id;
        this.maintenanceType = builder.maintenanceType;
        this.serviceProvider = builder.serviceProvider;
        this.serviceDate = builder.serviceDate;
    }

    public static Maintenance.Builder builder() {
        return new Maintenance.Builder();
    }

    public int getId() {
        return id;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "id=" + id +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", serviceDate=" + serviceDate + '\'' +
                '}';
    }

    public static class Builder {
        private int id;
        private String maintenanceType;
        private String serviceProvider;
        private LocalDate serviceDate;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setMaintenanceType(String maintenanceType) {
            this.maintenanceType = maintenanceType;
            return this;
        }

        public Builder setServiceProvider(String serviceProvider) {
            this.serviceProvider = serviceProvider;
            return this;
        }

        public Builder setServiceDate(LocalDate serviceDate) {
            this.serviceDate = serviceDate;
            return this;
        }

        public Builder copy(Maintenance maintenance) {
            this.id = maintenance.id;
            this.maintenanceType = maintenance.maintenanceType;
            this.serviceProvider = maintenance.serviceProvider;
            this.serviceDate = maintenance.serviceDate;
            return this;
        }

        public Maintenance build() {
            return new Maintenance(this);
        }

        public Builder id(int nextInt) {
            return null;
        }
    }
}





