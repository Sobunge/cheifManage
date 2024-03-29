package com.pensasha.cheifManage.account;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.pensasha.cheifManage.message.Message;
import com.pensasha.cheifManage.message.Status;
import com.pensasha.cheifManage.month.Month;
import com.pensasha.cheifManage.message.MessageService;
import com.pensasha.cheifManage.transaction.Transaction;
import com.pensasha.cheifManage.transaction.TransactionService;
import com.pensasha.cheifManage.user.User;
import com.pensasha.cheifManage.user.UserService;
import com.pensasha.cheifManage.year.YearService;

@Controller
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    YearService yearService;
    @Autowired
    private ServletContext servletContext;

    // private final String baseUrl = "http://localhost:8081/";
    private final String baseUrl = "https://national-chief.azurewebsites.net/";

    private final TemplateEngine templateEngine;

    public AccountController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    // Get all accounts
    @GetMapping("/accounts")
    public String getAccounts(Model model, Principal principal) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("account", new Account());
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

        return "accounts";
        
    }

    @GetMapping("/accounts/pdf")
    public ResponseEntity<?> getAccountsPdf(HttpServletRequest request, HttpServletResponse response,
            Principal principal) {

        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("accounts", accountService.allAccount());
        context.setVariable("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        context.setVariable("account", new Account());

        String accountsHtml = this.templateEngine.process("reports/accountsPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(accountsHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    // Get an account
    @GetMapping("/accounts/{id}")
    public String getAnAccount(@PathVariable String id, Model model, Principal principal) {

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("account", accountService.getAccount(id));
        model.addAttribute("transactions", transactionService.getAllTransactionForAccount(id));
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));

        List<User> missingUsers = new ArrayList<>();
        List<User> users = userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE);

        for (int i = 0; i < users.size(); i++) {
            if (!accountService.getAccount(id).getUsers().contains(users.get(i))) {
                missingUsers.add(users.get(i));
            }
        }

        model.addAttribute("missingUsers", missingUsers);

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "account";
    }

    @GetMapping("/usersHome")
    public String getHomePage() {

        return "redirect:/accounts";
    }

    // Save an account
    @PostMapping("/account")
    public RedirectView addAnAccount(@ModelAttribute Account account, RedirectAttributes redit) {

        if (!account.getName().equals("Monthly Contribution")) {
            if (!accountService.doesAccountExistByName(account.getName())) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-HHmmss");
                account.setId("ACC-" + sdf.format(date));
                account.setStatus(com.pensasha.cheifManage.user.Status.ACTIVE);

                List<User> users = (List<User>) userService.getAllUsers();
                if (!users.isEmpty()) {
                    account.setUsers(users);
                }

                accountService.addAccount(account);

                redit.addFlashAttribute("accountSuccess", "Account successfully created");
            } else {
                redit.addFlashAttribute("accountFail", account.getName() + " already exists");
            }

        } else {

            redit.addFlashAttribute("accountFail", "Monthly contribution already exists");
        }

        return new RedirectView("/accounts", true);

    }

    // Update an account
    @PostMapping("/accounts/{id}")
    public RedirectView updateAnAccount(@ModelAttribute Account account, @PathVariable String id,
            RedirectAttributes redit) {

        if (accountService.doesAccountExist(id)) {

            redit.addFlashAttribute("accountSuccess", "Account successfully created");

            Account existingAccount = accountService.getAccount(id);
            existingAccount.setDescription(account.getDescription());

            accountService.updateAccount(existingAccount);

        } else {
            redit.addFlashAttribute("accountFail", "Account does not exist.");
        }

        return new RedirectView("/accounts/" + id, true);
    }

    // Delete an Account
    @GetMapping("/account/{id}")
    public RedirectView deleteAnAccount(@PathVariable String id, RedirectAttributes redit) {

        accountService.deleteAccount(id);

        redit.addFlashAttribute("successAccount", "Account successfully deleted");

        return new RedirectView("/accounts", true);
    }

}
