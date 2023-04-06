package az.ac.cput.domain;
/**
 * Reservations.java
 * Class for the Reservations
 * Author: Cwenga Dlova (214310671)
 * Date:  01 April 2023
 */
import java.sql.Time;
import java.time.LocalDate;


public class Reservations {
    private int id;
    private String pickUpLocation;
    private LocalDate pickUpDate;
    private Time pickUpTime;
    private String returnLocation;
    private LocalDate returnDate;
    private Time returnTme;

    public Reservations(Builder builder) {
        this.id = builder.id;
        this.pickUpLocation = builder.pickUpLocation;
        this.pickUpDate = builder.pickUpDate;
        this.pickUpTime = builder.pickUpTime;
        this.returnLocation = builder.returnLocation;
        this.returnDate = builder.returnDate;
        this.returnTme = builder.returnTme;
    }

    public int getId() {
        return id;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public LocalDate getPickUpDate() {
        return pickUpDate;
    }

    public Time getPickUpTime() {
        return pickUpTime;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public Time getReturnTme() {
        return returnTme;
    }

    @Override
    public String toString() {
        return "Reservations{" +
                "id=" + id +
                ", pickUpLocation='" + pickUpLocation + '\'' +
                ", pickUpDate=" + pickUpDate +
                ", pickUpTime=" + pickUpTime +
                ", returnLocation='" + returnLocation + '\'' +
                ", returnDate=" + returnDate +
                ", returnTme=" + returnTme +
                '}';
    }
    public static class Builder{
        private int id;
        private String pickUpLocation;
        private LocalDate pickUpDate;
        private Time pickUpTime;
        private String returnLocation;
        private LocalDate returnDate;
        private Time returnTme;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setPickUpLocation(String pickUpLocation) {
            this.pickUpLocation = pickUpLocation;
            return this;
        }

        public Builder setPickUpDate(LocalDate pickUpDate) {
            this.pickUpDate = pickUpDate;
            return this;

        }

        public Builder setPickUpTime(Time pickUpTime) {
            this.pickUpTime = pickUpTime;
            return this;
        }

        public Builder setReturnLocation(String returnLocation) {
            this.returnLocation = returnLocation;
            return this;
        }

        public Builder setReturnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
            return this;
        }

        public Builder setReturnTme(Time returnTme) {
            this.returnTme = returnTme;
            return this;
        }
        public Builder copy(Reservations reservations) {
            this.id = reservations.id;
            this.pickUpLocation = reservations.pickUpLocation;
            this.pickUpDate = reservations.pickUpDate;
            this.pickUpTime = reservations.pickUpTime;
            this.returnLocation = reservations.returnLocation;
            this.returnDate = reservations.returnDate;
            this.returnTme = reservations.returnTme;
            return this;
        }
        public Reservations build() {
            return new Reservations(this);
        }
    }
}

