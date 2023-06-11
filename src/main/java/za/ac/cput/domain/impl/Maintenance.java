/* Maintenance.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 19 March 2023
*/
package za.ac.cput.domain.impl;

import za.ac.cput.domain.IDomain;

import java.time.LocalDate;

public class Maintenance implements IDomain {
    private int maintenanceId;
    private String maintenanceType;
    private String serviceProvider;
    private LocalDate serviceDate;


    private Maintenance(Builder builder) {
        this.maintenanceId = builder.maintenanceId;
        this.maintenanceType = builder.maintenanceType;
        this.serviceProvider = builder.serviceProvider;
        this.serviceDate = builder.serviceDate;
    }

    public int getMaintenanceId() {
        return maintenanceId;
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
    public static Maintenance.Builder builder() {
        return new Maintenance.Builder();
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "maintenanceId=" + maintenanceId +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", serviceDate=" + serviceDate +'\''+
                '}';
    }

    @Override
    public int getId() {
        return 0;
    }

    public static class Builder {
        private int maintenanceId;
        private String maintenanceType;
        private String serviceProvider;
        private LocalDate serviceDate;

        public Builder setMaintenanceId(int maintenanceId) {
            this.maintenanceId = maintenanceId;
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
            this.maintenanceId = maintenance.maintenanceId;
            this.maintenanceType = maintenance.maintenanceType;
            this.serviceProvider = maintenance.serviceProvider;
            this.serviceDate = maintenance.serviceDate;
            return this;
        }

        public Maintenance build() {
            return new Maintenance(this);
        }

        public Builder maintenanceId(int nextInt) {
            return null;
        }
    }
}





