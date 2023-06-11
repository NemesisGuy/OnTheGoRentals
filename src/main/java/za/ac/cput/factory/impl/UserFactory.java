package za.ac.cput.factory.impl;

import za.ac.cput.domain.impl.User;
import za.ac.cput.factory.IFactory;

public class UserFactory implements IFactory<User> {

    public User create(int id, String userName, String email, String profilePicture, String firstName, String lastName, String phoneNumber, String password, String role) {
        return User.builder()
                .id(id)
                .userName(userName)
                .email(email)
                .pictureUrl(profilePicture)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .password(password)
                .role(role)
                .build();

    }

    @Override
    public User create() {
        return User.builder().build();
    }
}

