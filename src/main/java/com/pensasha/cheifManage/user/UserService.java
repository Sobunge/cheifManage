package com.pensasha.cheifManage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    // Adding a user
    public User addUser(User user) {

        return userRepository.save(user);
    }

    // Updating user details
    public User updateUserDetails(User user, int idNumber) {
        User existingUser = userRepository.findById(idNumber).get();

        Account account = accountService.getAccountByUserIdNumber(idNumber);

        User tempUser = new User(user.getFirstName(), user.getSecondName(), user.getThirdName(),
                user.getGender(), idNumber, user.getEmail(), user.getResidentialAddress(), user.getCounty(),
                user.getDivision(), user.getLocation(), user.getSubLocation(), user.getTitle(), user.getPhoneNumber(),
                existingUser.getPassword(), existingUser.getRole());

        userRepository.save(tempUser);

        accountService.addAccount(account);

        return tempUser;
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
