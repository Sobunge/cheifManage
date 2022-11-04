package com.pensasha.cheifManage.main;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pensasha.cheifManage.user.User;
import com.pensasha.cheifManage.user.UserService;

@Controller
public class MainController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/homepage")
    public String homepage(Model model, Principal principal){

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        model.addAttribute("user", user);

        return "homepage";
    }
}
