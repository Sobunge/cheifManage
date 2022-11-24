package com.pensasha.cheifManage.securingWeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.role.Role;
import com.pensasha.cheifManage.user.Gender;
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
                Role.SUPER_ADMIN, null, null);
                User admin1 = new User("Jack", "Evans", "Madiwa", Gender.Male, 11111111, "samuelobunge@gmail.com", "Riat",
                "Kisumu", "Kogony", "Kisumu West", "Central", Title.CHIEF, 0700000000, encoder.encode("madiwa"),
                Role.SUPER_ADMIN, null, null);

        userService.addUser(admin);
        userService.addUser(admin1);

        if (accountService.doesAccountExist(admin.getIdNumber()) == false) {

            Account account = new Account();
            account.setId(admin.getIdNumber());
            account.setName("32906735");
            account.setDescription("My saving account");

            account.setUser(admin);
            accountService.addAccount(account);
        }
        if (accountService.doesAccountExist(admin1.getIdNumber()) == false) {

            Account account = new Account();
            account.setId(admin1.getIdNumber());
            account.setName("Jack Madiwa");
            account.setDescription("My saving account");

            account.setUser(admin1);
            accountService.addAccount(account);
        }


    }
}
