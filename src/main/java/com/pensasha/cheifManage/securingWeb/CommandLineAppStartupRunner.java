package com.pensasha.cheifManage.securingWeb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.role.Role;
import com.pensasha.cheifManage.user.Gender;
import com.pensasha.cheifManage.user.Office;
import com.pensasha.cheifManage.user.Status;
import com.pensasha.cheifManage.user.Title;
import com.pensasha.cheifManage.user.User;
import com.pensasha.cheifManage.user.UserService;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    public void run(String... args) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User admin = new User("Samuel", "Odhiambo", "Obunge", Gender.Male, 32906735, "samuelobunge@gmail.com", "Riat",
                "Kisumu", "Kogony", "Kisumu West", "Central", Title.CHIEF, 0707335375, encoder.encode("samuel1995"),
                Role.SUPER_ADMIN, Office.DELEGATES, Status.ACTIVE, null, null, null);
        User admin1 = new User("Jack", "Evans", "Madiwa", Gender.Male, 11111111, "samuelobunge@gmail.com", "Riat",
                "Kisumu", "Kogony", "Kisumu West", "Central", Title.CHIEF, 0700000000, encoder.encode("madiwa"),
                Role.SUPER_ADMIN, Office.DELEGATES, Status.ACTIVE, null, null, null);

        userService.addUser(admin);
        userService.addUser(admin1);

        if (!accountService.doesAccountExistByName("Monthly Contribution")) {
            Account account = new Account();
            Account regAccount  = new Account();

            Date date = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-HHmmss");
            account.setId("ACC-M-" + sdf.format(date));
            account.setStatus(Status.ACTIVE);
            regAccount.setId("ACC-R-" + sdf.format(date));
            regAccount.setStatus(Status.ACTIVE);

            account.setName("Monthly Contribution");
            account.setMinimumBalanace(300);

            regAccount.setName("Registration");
            regAccount.setMinimumBalanace(200);

            account.setDescription("An account for members monthly contributions");
            regAccount.setDescription("An account for members registration contributions");

            List<User> users = (List<User>) userService.getAllUsers();
            if (!users.isEmpty()) {
                account.setUsers(users);
                regAccount.setUsers(users);
            }

            accountService.addAccount(account);
            accountService.addAccount(regAccount);
        }

    }
}
