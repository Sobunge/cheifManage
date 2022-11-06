package com.pensasha.cheifManage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Adding a user
    public User addUser(User user) {

        return userRepository.save(user);
    }

    // Updating user details
    public User updateUserDetails(User user, int idNumber) {
        User existingUser = userRepository.findById(idNumber).get();

        User tempUser = new User(user.getFirstName(), user.getSecondName(), user.getThirdName(),
                user.getGender(), idNumber, user.getEmail(), user.getResidentialAddress(), user.getCounty(),
                user.getDivision(), user.getLocation(), user.getSubLocation(), user.getTitle(), user.getPhoneNumber(),
                existingUser.getPassword(), existingUser.getRole(), existingUser.getAccount());

        return userRepository.save(tempUser);
    }

    // Getting all users
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Getting a user by id number
    public User getUserByIdNumber(int idNumber) {
        return userRepository.findById(idNumber).get();
    }

    // Deleting a user
    public void deleteUserDetails(int idNumber) {

        userRepository.deleteById(idNumber);
    }

    // Does user exist
    public Boolean doesUserExist(int idNumber) {
        return userRepository.existsById(idNumber);
    }

}
