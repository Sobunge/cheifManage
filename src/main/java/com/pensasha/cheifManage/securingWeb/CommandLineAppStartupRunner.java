package com.pensasha.cheifManage.securingWeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pensasha.cheifManage.role.Role;
import com.pensasha.cheifManage.user.User;
import com.pensasha.cheifManage.user.UserService;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner{

    @Autowired
    private UserService userService;

    public void run(String... args) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User admin = new User("Samuel", "Odhiambo", "Obunge", 32906735, 0707335375, encoder.encode("samuel1995"), Role.SUPER_ADMIN);
        userService.addUser(admin);

    }
}
