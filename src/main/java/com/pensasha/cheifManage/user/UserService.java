package com.pensasha.cheifManage.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pensasha.cheifManage.role.Role;

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

        user.setPassword(existingUser.getPassword());
        user.setStatus(existingUser.getStatus());
        user.setSms(existingUser.getSms());
        user.setAccounts(existingUser.getAccounts());
        user.setMessages(existingUser.getMessages());

        if(existingUser.getRole() == Role.USER){
            user.setRole(existingUser.getRole());
        }

        userRepository.save(user);

        return user;
    }

    // Getting all users
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    //Get all active users
    public List<User> getAllActiveUsers(Status status){
        return userRepository.findAllByStatus(status);
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

    // User with a title
    public List<User> usersWithTitle(Title title) {
        return userRepository.findAllByTitle(title);
    }

    //Does a user with the office exist
    public Boolean doesUserWithOfficeExist(Office office){
        return userRepository.existsByOffice(office);
    }
}
