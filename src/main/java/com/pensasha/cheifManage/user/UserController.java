package com.pensasha.cheifManage.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.role.Role;
import com.pensasha.cheifManage.transaction.Transaction;
import com.pensasha.cheifManage.transaction.TransactionService;
import com.pensasha.cheifManage.year.YearService;
import com.pensasha.cheifManage.message.Message;
import com.pensasha.cheifManage.message.MessageService;
import com.pensasha.cheifManage.message.Status;
import com.pensasha.cheifManage.month.Month;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private YearService yearService;

    @Autowired
    private ServletContext servletContext;

    // private final String baseUrl = "http://localhost:8081/";
    private final String baseUrl = "https://national-chief.azurewebsites.net/";

    private final TemplateEngine templateEngine;

    public UserController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    private final LinkedHashMap<Gender, String> gender = new LinkedHashMap<Gender, String>() {
        {
            put(Gender.Male, "Male");
            put(Gender.Female, "Female");
        };
    };
    private final LinkedHashMap<Title, String> titles = new LinkedHashMap<Title, String>() {
        {
            put(Title.SNR_CHIEF, "Senior Chief");
            put(Title.SNR_ASS_CHIEF, "Senior Assistant Chief");
            put(Title.PRINCIPAL_CHIEF, "Principal Chief");
            put(Title.CHIEF, "Chief");
            put(Title.ASSISTANT_CHIEF, "Assistant Chief");
            put(Title.SNR_CHIEF_1, "Senior Chief I");
            put(Title.SNR_ASS_CHIEF_1, "Senior Assistant Chief I");
            put(Title.SNR_CHIEF_2, "Senior Chief II");
            put(Title.SNR_ASS_CHIEF_2, "Senior Assistant Chief II");

        };
    };
    private final LinkedHashMap<Role, String> roles = new LinkedHashMap<Role, String>() {
        {
            put(Role.ACCOUNTS_MANAGER, "Accounts Manager");
            put(Role.COUNTY_ADMIN, "County Admin");
            put(Role.SUPER_ADMIN, "Super Admin");
            put(Role.USER, "User");
        };
    };
    private final LinkedHashMap<Office, String> offices = new LinkedHashMap<>() {
        {
            put(Office.CHAIRMAN, "Chairman");
            put(Office.SECRETARY, "Secretary");
            put(Office.TREASURER, "Treasurer");
            put(Office.DELEGATES, "Delegates");
            put(Office.MEMBER, "Member");
        };
    };

    private List<User> activeUsers(List<User> users) {

        List<User> activeUsers = new ArrayList<>();

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (!account.getName().equals("Monthly Contribution")) {
                    for (Transaction transaction : account.getTransactions()) {
                        if (transaction.getUser().equals(user)) {
                            activeUsers.add(user);
                            break;
                        }
                    }
                }
            }
        }

        return activeUsers;
    }

    // Get all user
    @GetMapping("/users")
    public String gettingAllUsers(Model model, Principal principal) {

        model.addAttribute("title", "Users");
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "users";
    }

    // Getting all active users
    @GetMapping("/activeUsers")
    public String getAllActiveUsers(Model model, Principal principal) {

        List<User> users = userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE);

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }

        model.addAttribute("messages", messages);
        model.addAttribute("users", activeUsers(users));
        model.addAttribute("messageCount", count);
        model.addAttribute("title", "Active Users");
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        return "users";
    }

    // Getting all inactive users
    @GetMapping("/inActiveUsers")
    public String getAllInActiveUsers(Model model, Principal principal) {

        List<User> inactiveUsers = new ArrayList<>();
        List<User> users = userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE);

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }

        for (User user : users) {
            if (!activeUsers(users).contains(user)) {
                inactiveUsers.add(user);
            }
        }

        model.addAttribute("messages", messages);
        model.addAttribute("users", inactiveUsers);
        model.addAttribute("messageCount", count);
        model.addAttribute("title", "Inactive Users");
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        return "users";
    }

    @GetMapping("/users/pdf")
    public ResponseEntity<?> getUsersReport(HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, this.servletContext);

        context.setVariable("title", "Users");
        context.setVariable("users", userService.getAllUsers());

        String usersListHtml = this.templateEngine.process("reports/usersListPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(usersListHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    @GetMapping("/activeUsers/pdf")
    public ResponseEntity<?> getActiveUsersReport(HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, this.servletContext);
        List<User> users = userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE);

        context.setVariable("title", "Active Users");
        context.setVariable("users", activeUsers(users));

        String usersListHtml = this.templateEngine.process("reports/usersListPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(usersListHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    @GetMapping("/inActiveUsers/pdf")
    public ResponseEntity<?> getInActiveUsersReport(HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, this.servletContext);
        List<User> inactiveUsers = new ArrayList<>();
        List<User> users = userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE);

        for (User user : users) {
            if (!activeUsers(users).contains(user)) {
                inactiveUsers.add(user);
            }
        }

        context.setVariable("title", "Inactive Users");
        context.setVariable("users", inactiveUsers);

        String usersListHtml = this.templateEngine.process("reports/usersListPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(usersListHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    @GetMapping("/user")
    public String addUserGet(Model model, Principal principal, @ModelAttribute("newUser") User newUser) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("newUser", newUser);
        model.addAttribute("genders", gender);
        model.addAttribute("titles", titles);
        model.addAttribute("roles", roles);
        model.addAttribute("offices", offices);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "addUser";
    }

    @GetMapping("/user/pdf")
    public ResponseEntity<?> getRegistrationForm(HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, this.servletContext);

        String registrationHtml = this.templateEngine.process("reports/registrationPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(registrationHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
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
            if (newUser.getOffice().equals(Office.CHAIRMAN) & userService.doesUserWithOfficeExist(Office.CHAIRMAN)) {
                redit.addFlashAttribute("fail", "A Chairman already exists");
                redit.addFlashAttribute("newUser", newUser);
                redirectView = new RedirectView("/user", true);
            } else if (newUser.getOffice().equals(Office.SECRETARY)
                    & userService.doesUserWithOfficeExist(Office.SECRETARY)) {
                redit.addFlashAttribute("fail", "A Secretary already exists");
                redit.addFlashAttribute("newUser", newUser);
                redirectView = new RedirectView("/user", true);
            } else if (newUser.getOffice().equals(Office.TREASURER)
                    & userService.doesUserWithOfficeExist(Office.TREASURER)) {
                redit.addFlashAttribute("fail", "A Treasurer already exists");
                redit.addFlashAttribute("newUser", newUser);
                redirectView = new RedirectView("/user", true);
            } else {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                newUser.setPassword(encoder.encode(String.valueOf(newUser.getIdNumber())));
                newUser.setStatus(com.pensasha.cheifManage.user.Status.ACTIVE);

                userService.addUser(newUser);

                List<Account> accounts = accountService
                        .allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE);

                for (Account account : accounts) {
                    List<User> accountUsers = account.getUsers();
                    accountUsers.add(newUser);
                    account.setUsers(accountUsers);
                    accountService.addAccount(account);
                }

                redit.addFlashAttribute("success",
                        newUser.getFirstName() + " " + newUser.getThirdName() +
                                " successfully added");
                redirectView = new RedirectView("/users", true);
            }

        }

        return redirectView;

    }

    // CHanging user password
    @PostMapping("/user/{idNumber}/changePassword")
    public RedirectView changePassword(@PathVariable int idNumber, RedirectAttributes redit,
            @RequestParam String currentPass, @RequestParam String newPass) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userService.getUserByIdNumber(idNumber);

        if (encoder.matches(currentPass, user.getPassword())) {

            user.setPassword(encoder.encode(newPass));
            userService.addUser(user);

            redit.addFlashAttribute("success", "Password successfully changed");
        } else {
            redit.addFlashAttribute("fail", "Password entered does not match current password");
        }

        return new RedirectView("/users/" + idNumber, true);
    }

    // Deleting a user
    @GetMapping("/user/{idNumber}")
    public RedirectView deleteUser(@PathVariable int idNumber, RedirectAttributes redit) {

        if (userService.doesUserExist(idNumber) != true) {
            redit.addFlashAttribute("fail", "A user of id number: " + idNumber + " does not exist");
        } else {

            User user = userService.getUserByIdNumber(idNumber);
            List<Account> accounts = accountService.getUsersAccounts(idNumber);
            if (!accounts.isEmpty()) {
                for (Account account : accounts) {

                    account.getUsers().remove(user);
                    List<Transaction> transactions = transactionService.getAllUserTransaction(idNumber);
                    for (Transaction transaction : transactions) {
                        transactionService.deleteTransaction(transaction.getId());
                    }

                    accountService.addAccount(account);
                }

            }

            userService.deleteUserDetails(idNumber);
            redit.addFlashAttribute("success",
                    user.getFirstName() + " " + user.getThirdName() + " successfully removed");
        }

        return new RedirectView("/users", true);
    }

    // Getting a single User
    @GetMapping("/users/{idNumber}")
    public String viewUser(@PathVariable int idNumber, Model model, Principal principal) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("newUser", userService.getUserByIdNumber(idNumber));
        model.addAttribute("genders", gender);
        model.addAttribute("titles", titles);
        model.addAttribute("roles", roles);
        model.addAttribute("offices", offices);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "userProfile";

    }

    // Updating user details
    @PostMapping("/users/{idNumber}")
    public RedirectView updateUserDetails(@PathVariable int idNumber, @ModelAttribute User user,
            RedirectAttributes redit) {

        if (userService.doesUserExist(idNumber)) {
            userService.updateUserDetails(user, idNumber);

            redit.addFlashAttribute("success", "Details successfully updated");
        } else {
            redit.addFlashAttribute("fail", "A user of id number:" + idNumber + " does not exist");
        }

        return new RedirectView("/users/" + idNumber, true);
    }

}
