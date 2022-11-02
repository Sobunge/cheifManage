package com.pensasha.cheifManage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Get all user
    @GetMapping("/users")
    public String gettingAllUsers(Model model) {

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", new User());

        return "users";
    }

    // Adding user details
    @PostMapping("/user")
    public String addUser(@ModelAttribute User user, Model model) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(String.valueOf(user.getIdNumber())));

        userService.addUser(user);

        model.addAttribute("user", new User());
        model.addAttribute("users", userService.getAllUsers());

        return "users";

    }

    // Deleting a user
    @GetMapping("/user/{idNumber}")
    public String deleteUser(@PathVariable int idNumber, Model model) {

        userService.deleteUserDetails(idNumber);

        return "redirect:/users";
    }

    // Getting a single User
    @GetMapping("/users/{idNumber}")
    public String viewUser(@PathVariable int idNumber, Model model) {

        model.addAttribute("user", userService.getUserByIdNumber(idNumber));

        return "user";
    }

    // Updating user details
    @PostMapping("/users/{idNumber}")
    public String updateUserDetails(@PathVariable int idNumber, @ModelAttribute User user) {

        userService.updateUserDetails(user, idNumber);

        return "redirect:/users/" + idNumber;
    }

}
