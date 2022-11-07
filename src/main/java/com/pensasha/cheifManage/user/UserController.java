package com.pensasha.cheifManage.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.role.Role;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    private final Gender[] gender = { Gender.Male, Gender.Female };
    private final Title[] title = { Title.CHIEF, Title.ASSISTANT_CHIEF};
    private final Role[] role = { Role.ACCOUNTS_MANAGER, Role.COUNTY_ADMIN, Role.SUPER_ADMIN, Role.USER };

    // Get all user
    @GetMapping("/users")
    public String gettingAllUsers(Model model, Principal principal) {

        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("users", userService.getAllUsers());

        return "users";
    }

    @GetMapping("/user")
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
    public RedirectView addUser(@ModelAttribute User newUser, RedirectAttributes redit) {

        RedirectView redirectView;

        if (userService.doesUserExist(newUser.getIdNumber())) {
            redit.addFlashAttribute("fail", "A user of id number:" + newUser.getIdNumber() + " already exists");
            redit.addFlashAttribute("newUser", newUser);
            redirectView = new RedirectView("/user", true);
        } else {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            newUser.setPassword(encoder.encode(String.valueOf(newUser.getIdNumber())));

            Account account = new Account();
            account.setId(newUser.getIdNumber());
            account.setName(String.valueOf(newUser.getIdNumber()));
            account.setDescription("My saving account");
            account.setUser(newUser);

            userService.addUser(newUser);
            accountService.addAccount(account);

            redit.addFlashAttribute("success",
                    newUser.getFirstName() + " " + newUser.getThirdName() + " successfully added");
            redirectView = new RedirectView("/users", true);
        }

        return redirectView;

    }

    // Deleting a user
    @GetMapping("/user/{idNumber}")
    public RedirectView deleteUser(@PathVariable int idNumber, RedirectAttributes redit) {

        if(userService.doesUserExist(idNumber) != true){
            redit.addFlashAttribute("fail", "A user of id number: " + idNumber + " does not exist");
        }else{

            User user = userService.getUserByIdNumber(idNumber);
            
            Account account = accountService.getAccountByUserIdNumber(idNumber);
            accountService.deleteAccount(account.getId());

            userService.deleteUserDetails(idNumber);
            redit.addFlashAttribute("success", user.getFirstName() + " " + user.getThirdName() + " successfully removed");
        }

        return new RedirectView("/users", true);
    }

    // Getting a single User
    @GetMapping("/users/{idNumber}")
    public String viewUser(@PathVariable int idNumber, Model model, Principal principal) {

        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("newUser", userService.getUserByIdNumber(idNumber));
        model.addAttribute("genders", gender);
        model.addAttribute("titles", title);
        model.addAttribute("roles", role);

        return "userProfile";
    }

    // Updating user details
    @PostMapping("/users/{idNumber}")
    public RedirectView updateUserDetails(@PathVariable int idNumber, @ModelAttribute User user, RedirectAttributes redit) {

        if(userService.doesUserExist(idNumber)){
            userService.updateUserDetails(user, idNumber);

            redit.addFlashAttribute("success", "Details successfully updated");
        }else{
            redit.addFlashAttribute("fail", "A user of id number:" + idNumber + " does not exist");
        }

        return new RedirectView("/users/" + idNumber, true);
    }

}
