package com.pensasha.cheifManage.account;

import java.security.Principal;

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
import com.pensasha.cheifManage.transaction.Transaction;
import com.pensasha.cheifManage.transaction.TransactionService;
import com.pensasha.cheifManage.user.UserService;


@Controller
public class AccountController {
    
    @Autowired AccountService accountService;
    @Autowired TransactionService transactionService;
    @Autowired UserService userService;

    @Autowired
    private ServletContext servletContext;

    private final String baseUrl = "http://localhost:8081/";
    //private final String baseUrl = "https://analytica-school.herokuapp.com/";

    private final TemplateEngine templateEngine;
    
    public AccountController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    //Get all accounts
    @GetMapping("/accounts")
    public String getAccounts(Model model, Principal principal){
   
        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("account", new Account());
        model.addAttribute("accounts", accountService.allAccount());
        
        return "accounts";
    }

    @GetMapping("/accounts/pdf")
    public ResponseEntity<?> getAccountsPdf(HttpServletRequest request, HttpServletResponse response){

        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("accounts", accountService.allAccount());

        String accountsHtml = this.templateEngine.process("reports/accountsPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(accountsHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes); 
    }

    //Get an account
    @GetMapping("/accounts/{id}")
    public String getAnAccount(@PathVariable Integer id, Model model, Principal principal){

        model.addAttribute("user", userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("account", accountService.getAccount(id));
        model.addAttribute("transactions", transactionService.getAllTransactionForAccount(id));

        return "account";
    }

    //Save an account
    @PostMapping("/account")
    public RedirectView addAnAccount(@ModelAttribute Account account, RedirectAttributes redit){

        if(accountService.doesAccountExist(account.getId())){
            redit.addFlashAttribute("accountFail", "Account already exists");
        }else{

            accountService.addAccount(account);

            redit.addFlashAttribute("accountSuccess", "Account successfully created");
        }
        

        return new RedirectView("/accounts", true);
    }

    //Update an account
    @PostMapping("/accounts/{id}")
    public RedirectView updateAnAccount(@ModelAttribute Account account,@PathVariable int id, RedirectAttributes redit){

        if(accountService.doesAccountExist(id)){
            redit.addFlashAttribute("accountSuccess", "Account successfully created");
       
            Account existingAccount = accountService.getAccount(id);
            existingAccount.setDescription(account.getDescription());
    
            accountService.updateAccount(existingAccount);
        }else{
            redit.addFlashAttribute("accountFail", "Account does not exist.");
        }

       

        return new RedirectView("/accounts/" + id, true);
    }

    //Delete an Account
    @GetMapping("/account/{id}")
    public RedirectView deleteAnAccount(@PathVariable Integer id, RedirectAttributes redit){

        accountService.deleteAccount(id);

        redit.addFlashAttribute("successAccount", "Account successfully deleted");

        return new RedirectView("/accounts", true);
    }

}