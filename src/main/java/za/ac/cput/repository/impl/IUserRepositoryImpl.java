package za.ac.cput.repository.impl;


import za.ac.cput.domain.impl.User;
import za.ac.cput.repository.IRepository;

import java.util.ArrayList;
import java.util.List;

public class IUserRepositoryImpl implements IRepository<User, Integer> {
    private static IUserRepositoryImpl repository = null;
    private List<User> users;

    private IUserRepositoryImpl() {
        users = createUserList();
    }

    public static IUserRepositoryImpl getRepository() {
        if (repository == null)
            repository = new IUserRepositoryImpl();
        return repository;
    }

    @Override
    public User create(User entity) {
        if (users.add(entity)) {
            return entity;
        }
        return null;
    }

    @Override
    public User read(Integer id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User update(User entity) {
        User userToUpdate = read(entity.getId());
        if (userToUpdate != null) {
            users.remove(userToUpdate);
            users.add(entity);
            return entity;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        User userToDelete = read(id);
        if (userToDelete != null) {
            users.remove(userToDelete);
            return true;
        }
        return false;
    }


    public List<User> getAllUsers() {
        return users;
    }

    // ToDO Add more methods as needed...

    private ArrayList<User> createUserList() {
        ArrayList<User> users = new ArrayList<>();

        // Add users to the list...
        User user1 = User.builder()
                .userName("Scott.Snyder")
                .email("ScottSnyder@example.com")
                .pictureUrl("picture1.jpg")
                .firstName("Scott")
                .lastName("Snyder")
                .phoneNumber("123456789")
                .password("password")
                .role("admin")
                .build();
        users.add(user1);

        User user2 = User.builder()
                .userName("Chris.Claremont")
                .email("Chris.Claremont@example.com")
                .pictureUrl("picture2.jpg")
                .firstName("Chris")
                .lastName("Claremont")
                .phoneNumber("987654321")
                .password("password")
                .role("user")
                .build();
        users.add(user2);

        User user3 = User.builder()
                .userName("Gail.Simone")
                .email("Gail@Simone.com")
                .pictureUrl("picture3.jpg")
                .firstName("Gail")
                .lastName("Simone")
                .phoneNumber("555555555")
                .password("password")
                .role("user")
                .build();
        users.add(user3);

        User user4 = User.builder()
                .userName("Roy.Thomas")
                .email("Roy@Thomas.com")
                .pictureUrl("picture4.jpg")
                .firstName("Roy")
                .lastName("Thomas")
                .phoneNumber("999999999")
                .password("password")
                .role("user")
                .build();
        users.add(user4);

        User user5 = User.builder()
                .userName("Jonathan.Hickman")
                .email("mike@example.com")
                .pictureUrl("picture5.jpg")
                .firstName("Jonathan")
                .lastName("Hickman")
                .phoneNumber("777777777")
                .password("password")
                .role("user")
                .build();
        users.add(user5);

        User user6 = User.builder()
                .userName("jim.starlin")
                .email("jim@example.com")
                .pictureUrl("picture6.jpg")
                .firstName("jim")
                .lastName("starlin")
                .phoneNumber("888888888")
                .password("password")
                .role("user")
                .build();
        users.add(user6);

        User user7 = User.builder()
                .userName("BrianMichael.Bendis")
                .email("BrianMichael@Bendis.com")
                .pictureUrl("picture7.jpg")
                .firstName("BrianMichael")
                .lastName("Bendis")
                .phoneNumber("444444444")
                .password("password")
                .role("user")
                .build();
        users.add(user7);

        User user8 = User.builder()
                .userName("dan.slot")
                .email("dan@example.com")
                .pictureUrl("picture8.jpg")
                .firstName("dan")
                .lastName("slot")
                .phoneNumber("222222222")
                .password("password")
                .role("user")
                .build();
        users.add(user8);

        User user9 = User.builder()
                .userName("jack.kirby")
                .email("jack@kirby.com")
                .pictureUrl("picture8.jpg")
                .firstName("jack")
                .lastName("kirby")
                .phoneNumber("222222222")
                .password("password")
                .role("user")
                .build();
        users.add(user9);

        User user10 = User.builder()
                .userName("stan.lee")
                .email("stanlee@example.com")
                .pictureUrl("picture8.jpg")
                .firstName("stan")
                .lastName("lee")
                .phoneNumber("222222222")
                .password("password")
                .role("user")
                .build();
        users.add(user10);

        // Add more users to the list...

        return users;
    }

}
