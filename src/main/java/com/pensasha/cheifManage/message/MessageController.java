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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.month.Month;
import com.pensasha.cheifManage.transaction.Transaction;
import com.pensasha.cheifManage.user.Title;
import com.pensasha.cheifManage.user.UserService;
import com.pensasha.cheifManage.year.YearService;
import com.pensasha.cheifManage.user.User;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private YearService yearService;

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

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("title", "Sent Messages");
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        List<Message> sentMessages = messageService.getMySentMessages(idNumber);
        model.addAttribute("sentMessages", sentMessages);

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "messages";
    }

    // Get all messages received as a user
    @GetMapping("/users/{idNumber}/recievedMessages")
    public String getMyRecievedMessages(@PathVariable int idNumber, Principal principal, Model model) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("title", "Inbox");
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));
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

        return "messages";
    }

    // Get a message
    @GetMapping("/users/{idNumber}/messages/{id}")
    public String getMessage(@PathVariable int idNumber, @PathVariable Long id, Model model, Principal principal) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));
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

        return "message";
    }

    @GetMapping("/users/{idNumber}/message")
    public String composeMessage(@PathVariable int idNumber, Model model, Principal principal) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("titles", titles);
        model.addAttribute("mail", new Message());
        model.addAttribute("user", userService.getUserByIdNumber(idNumber));
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

        return "compose";
    }

    // send a message
    @PostMapping("/users/{idNumber}/message")
    public RedirectView sendMessage(HttpServletRequest request, @PathVariable int idNumber,
            @ModelAttribute Message mail,
            RedirectAttributes redit, Principal principal) {

        String output;

        List<User> users = new ArrayList<>();
        for (Title t : Title.values()) {
            if (request.getParameter(t.name() + "Input") != null) {
                users.addAll(userService.usersWithTitle(t));
                users.remove(userService.getUserByIdNumber(idNumber));
            }
        }

        if (users.isEmpty()) {

            redit.addFlashAttribute("fail", "Your message should have a recipient");
            output = "/users/" + idNumber + "/message";
        } else {
            if (mail.getMail().isEmpty()) {
                redit.addFlashAttribute("fail", "Your message must not be empty");
                output = "/users/" + idNumber + "/message";
            } else {

                Date date = new Date();
                User sender = userService.getUserByIdNumber(idNumber);

                mail.setSendersName(
                        sender.getTitle().name() + ", " + sender.getFirstName() + ' ' + sender.getThirdName());
                mail.setStatus(Status.UNREAD);
                mail.setSender(sender);
                mail.setDate(date);
                mail.setRecievers(users);

                messageService.sendMessage(mail);

                redit.addFlashAttribute("success", "Your message was successfully sent");

                output = "/users/" + idNumber + "/sentMessages";
            }
        }

        return new RedirectView(output, true);

    }

    // delete a message
    @GetMapping("/users/{idNumber}/message/{id}")
    public RedirectView deleteMessage(@PathVariable int idNumber, @PathVariable Long id, RedirectAttributes redit) {

        redit.addFlashAttribute("success", "Message successfully deleted");

        return new RedirectView("/users/" + idNumber + "/recievedMessages", true);
    }
}
