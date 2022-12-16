package com.pensasha.cheifManage.securingWeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

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

    public void run(String... args) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User admin = new User("Samuel", "Odhiambo", "Obunge", Gender.Male, 32906735, "samuelobunge@gmail.com", "Riat",
                "Kisumu", "Kogony", "Kisumu West", "Central", Title.CHIEF, 0707335375, encoder.encode("samuel1995"),
                Role.SUPER_ADMIN, Office.CHAIRMAN, Status.ACTIVE, null, null, null);
                User admin1 = new User("Jack", "Evans", "Madiwa", Gender.Male, 11111111, "samuelobunge@gmail.com", "Riat",
                "Kisumu", "Kogony", "Kisumu West", "Central", Title.CHIEF, 0700000000, encoder.encode("madiwa"),
                Role.SUPER_ADMIN, Office.TREASURER, Status.ACTIVE, null, null, null);

        userService.addUser(admin);
        userService.addUser(admin1);

    }
}
