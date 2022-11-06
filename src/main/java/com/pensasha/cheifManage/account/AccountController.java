package com.pensasha.cheifManage.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.pensasha.cheifManage.transaction.Transaction;
import com.pensasha.cheifManage.transaction.TransactionService;
import com.pensasha.cheifManage.user.UserService;


@Controller
public class AccountController {
    
    @Autowired AccountService accountService;
    @Autowired TransactionService transactionService;
    @Autowired UserService userService;

    //Get all accounts
    @GetMapping("/accounts")
    public String getAccounts(Model model){
   
        model.addAttribute("account", new Account());
        model.addAttribute("accounts", accountService.allAccount());
        
        return "accounts";
    }

    //Get an account
    @GetMapping("/accounts/{id}")
    public String getAnAccount(@PathVariable Long id, Model model){

        model.addAttribute("transaction", new Transaction());
        model.addAttribute("account", accountService.getAccount(id));
        model.addAttribute("transactions", transactionService.getAllTransactionForAccount(id));

        return "account";
    }

    //Save an account
    @PostMapping("/account")
    public String addAnAccount(@ModelAttribute Account account){

        accountService.addAccount(account);

        return "redirect:/accounts";
    }

    //Update an account
    @PostMapping("/accounts/{id}")
    public String updateAnAccount(@ModelAttribute Account account,@PathVariable Long id){

        accountService.updateAccount(account);

        return "redirect:/accounts/" + id;
    }

    //Delete an Account
    @GetMapping("/account/{id}")
    public String deleteAnAccount(@PathVariable Long id){

        accountService.deleteAccount(id);

        return "redirect:/accounts";
    }

}