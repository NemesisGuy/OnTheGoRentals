package za.ac.cput.factory.impl;
/**
 * AboutUsFactory.java
 * Factory Class for About us
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 */

import za.ac.cput.domain.entity.AboutUs;

public class AboutUsFactory {

    public static AboutUs createAboutUs(int id, String address, String officeHours, String email, String telephone, String whatsApp) {
        return new AboutUs.Builder()
                .setId(id)
                .setAddress(address)
                .setOfficeHours(officeHours)
                .setEmail(email)
                .setTelephone(telephone)
                .setWhatsApp(whatsApp)
                .build();
    }

    public AboutUs create() {
        return new AboutUs.Builder().build();
    }
}

