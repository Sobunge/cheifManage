package com.pensasha.cheifManage.user;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pensasha.cheifManage.role.Role;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    private final Gender[] gender = { Gender.Male, Gender.Female };
    private final Title[] title = { Title.CHIEF, Title.ASSISTANT_CHIEF, Title.MR, Title.MRS, Title.MS };
    private final Role[] role = { Role.ACCOUNTS_MANAGER, Role.COUNTY_ADMIN, Role.SUPER_ADMIN, Role.USER };

    // Get all user
    @GetMapping("/users")
    public String gettingAllUsers(Model model, Principal principal) {

        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("newUser", new User());

        return "users";
    }

    @GetMapping("user")
    public String addUserGet(Model model, Principal principal) {

        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("newUser", new User());
        model.addAttribute("genders", gender);
        model.addAttribute("titles", title);
        model.addAttribute("roles", role);

        return "addUser";
    }

    // Adding user details
    @PostMapping("/user")
    @ResponseBody
    public User addUser(@ModelAttribute User newUser, Model model) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newUser.setPassword(encoder.encode(String.valueOf(newUser.getIdNumber())));

        // userService.addUser(newUser);

        return newUser;

        // return "redirect:/users";

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

        User user = userService.getUserByIdNumber(idNumber);

        model.addAttribute("user", user);
        model.addAttribute("newUser", user);
        model.addAttribute("genders", gender);
        model.addAttribute("titles", title);
        model.addAttribute("roles", role);

        return "userProfile";
    }

    // Updating user details
    @PostMapping("/users/{idNumber}")
    public String updateUserDetails(@PathVariable int idNumber, @ModelAttribute User user) {

        userService.updateUserDetails(user, idNumber);

        return "redirect:/users/" + idNumber;
    }

}
