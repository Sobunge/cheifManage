package com.pensasha.cheifManage.message;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.cheifManage.user.Title;
import com.pensasha.cheifManage.user.UserService;
import com.pensasha.cheifManage.user.User;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

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
    public String composeMessage(@PathVariable int idNumber, Model model, Principal principal) {

        model.addAttribute("titles", titles);
        model.addAttribute("mail", new Message());
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));

        return "compose";
    }

    // send a message
    @PostMapping("/users/{idNumber}/message")
    public RedirectView sendMessage(HttpServletRequest request, @PathVariable int idNumber, @ModelAttribute Message mail,
            RedirectAttributes redit, Principal principal) {

        mail.setStatus(Status.UNREAD);
        mail.setSender(idNumber);

        Date date = new Date();
        mail.setDate(date);

        List<User> users = new ArrayList<>();
        for (Title t : Title.values()) {
            if (request.getParameter(t.name() + "Input") != null) {
                users.addAll(userService.usersWithTitle(t));
            }
        }
        mail.setRecievers(users);

        messageService.sendMessage(mail);
        
        redit.addFlashAttribute("success", "Your message was successfully sent");

        return new RedirectView("/users/" + idNumber + "/recievedMessages", true);
    }

    // delete a message
    @GetMapping("/users/{idNumber}/message/{id}")
    public RedirectView deleteMessage(@PathVariable int idNumber, @PathVariable Long id, RedirectAttributes redit) {

        redit.addFlashAttribute("success", "Message successfully deleted");

        return new RedirectView("/users/" + idNumber + "/recievedMessages", true);
    }
}
