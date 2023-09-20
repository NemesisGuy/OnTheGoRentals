package za.ac.cput.factory.impl;
/**
 * DamageReportFactory.java
 * Factory Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * */
import za.ac.cput.domain.DamageReport;
import za.ac.cput.domain.Rental;

import java.time.LocalDateTime;

public class DamageReportFactory {

    public DamageReport create(){
        return  new DamageReport.Builder().build();
    }

    public static DamageReport createReport(int id, Rental rental, String description, LocalDateTime dateAndTime, String location, double repairCost){
        return new DamageReport.Builder().setId(id)
                .setRental(rental)
                .setDescription(description)
                .setDateAndTime(dateAndTime)
                .setLocation(location)
                .setRepairCost(repairCost)
                .build();

    }

}
