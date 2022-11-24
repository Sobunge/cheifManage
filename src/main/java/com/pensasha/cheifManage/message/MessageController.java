package com.pensasha.cheifManage.message;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.cheifManage.user.UserService;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // Get all messages sent by user
    @GetMapping("/users/{idNumber}/sentMessages")
    public String getMySentMessages(@PathVariable int idNumber, Principal principal, Model model) {

        model.addAttribute("title", "Sent Messages");
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));
        model.addAttribute("messages", messageService.getMySentMessages(idNumber));

        return "messages";
    }

    // Get all messages received as a user
    @GetMapping("/users/{idNumber}/recievedMessages")
    public String getMyRecievedMessages(@PathVariable int idNumber, Principal principal, Model model) {

        model.addAttribute("title", "Inbox");
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));
        model.addAttribute("messages", messageService.getMyMessages(idNumber));

        return "messages";
    }

    // Get a message
    @GetMapping("/users/{idNumber}/messages/{id}")
    public String getMessage(@PathVariable int idNumber, @PathVariable Long id, Model model, Principal principal) {

        model.addAttribute("user", userService.getUserByIdNumber(idNumber));

        return "message";
    }

    @GetMapping("/users/{idNumber}/message")
    public String composeMessage(@PathVariable int idNumber, Model model, Principal principal){

        model.addAttribute("user", userService.getUserByIdNumber(idNumber));

        return "compose";
    }

    // send a message
    @PostMapping("/users/{idNumber}/message")
    public RedirectView sendMessage(@PathVariable int idNumber, @RequestBody Message message) {

        return new RedirectView("messages", true);
    }

    // delete a message
    @GetMapping("/users/{idNumber}/message/{id}")
    public RedirectView deleteMessage(@PathVariable int idNumber, @PathVariable Long id) {

        return new RedirectView("messages", true);
    }
}
