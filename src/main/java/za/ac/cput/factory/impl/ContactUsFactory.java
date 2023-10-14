package za.ac.cput.factory.impl;

/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 * */

import za.ac.cput.domain.ContactUs;

public class ContactUsFactory {

    public ContactUs create(){
        return new ContactUs.Builder().build();
    }

    public static ContactUs buildContactUs(int id, String title, String firstName, String lastName, String email, String subject, String message){
        return new ContactUs.Builder()
                .setId(id)
                .setTitle(title)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setSubject(subject)
                .setMessage(message)
                .build();
    }
}
