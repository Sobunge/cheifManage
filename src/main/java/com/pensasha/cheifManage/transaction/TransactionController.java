package com.pensasha.cheifManage.transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.month.Month;
import com.pensasha.cheifManage.user.User;
import com.pensasha.cheifManage.user.UserService;
import com.pensasha.cheifManage.year.Year;
import com.pensasha.cheifManage.year.YearService;


@Controller
public class TransactionController {

    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;
    @Autowired
    UserService userService;
    @Autowired
    YearService yearService;

    // Adding a transaction
    @PostMapping("/accounts/{id}/transaction")
    public String addTransaction(@PathVariable Long id, @ModelAttribute Transaction transaction,
            HttpServletRequest request) {

        Account account = accountService.getAccount(id);
        account.setBalance(account.getBalance() + transaction.getAmount());

        User user = userService.getUserByIdNumber(Integer.parseInt(request.getParameter("user")));

        Date date = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Nairobi/Kenya"));
        calendar.setTime(date);

        Year year;
        Set<Month> months;
        Short y = (short) calendar.get(Calendar.YEAR);
        if (yearService.doesYearExist(y)) {
            year = yearService.getYear(y);
            months = year.getMonths();
        } else {
            year = new Year();
            year.setYear(y);
            months = new HashSet<>();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        SimpleDateFormat tFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy");

        switch (sdf.format(calendar.get(Calendar.MONTH))) {
            case "January":
                months.add(Month.JANUARY);
                transaction.setMonth(Month.JANUARY);
                break;
            case "February":
                months.add(Month.FEBRUARY);
                transaction.setMonth(Month.FEBRUARY);
                break;
            case "March":
                months.add(Month.MARCH);
                transaction.setMonth(Month.MARCH);
                break;
            case "April":
                months.add(Month.APRIL);
                transaction.setMonth(Month.APRIL);
                break;
            case "May":
                months.add(Month.MAY);
                transaction.setMonth(Month.MAY);
                break;
            case "June":
                months.add(Month.JUNE);
                transaction.setMonth(Month.JUNE);
                break;
            case "July":
                months.add(Month.JULY);
                transaction.setMonth(Month.JULY);
                break;
            case "August":
                months.add(Month.AUGUST);
                transaction.setMonth(Month.AUGUST);
                break;
            case "September":
                months.add(Month.SEPTEMBER);
                transaction.setMonth(Month.SEPTEMBER);
                break;
            case "October":
                months.add(Month.OCTOBER);
                transaction.setMonth(Month.OCTOBER);
                break;
            case "November":
                months.add(Month.NOVEMBER);
                transaction.setMonth(Month.NOVEMBER                                             );
                break;
            default:
                months.add(Month.DECEMBER);
                transaction.setMonth(Month.DECEMBER);
        }

        year.setMonths(months);
        yearService.saveYear(year);

        transaction.setAccount(account);
        transaction.setUser(user);
        transaction.setYear(year);
        transaction.setDate(dFormat.format(date));
        transaction.setTime(tFormat.format(date));

        transactionService.addTransaction(transaction);

        return "redirect:/accounts/" + id;

    }

    //Deleting a transaction
    @GetMapping("/accounts/{id}/transactions/{trans_id}")
    public String deleteTransactionFromAccount(@PathVariable Long id, @PathVariable Long trans_id)
    {

        Account account = accountService.getAccount(trans_id);
        Transaction transaction = transactionService.getTransaction(trans_id);
        account.setBalance(account.getBalance() - transaction.getAmount());

        accountService.updateAccount(account);

        transactionService.deleteTransaction(trans_id);

        return "redirect:/accounts/" + id;
    }

    //Getting a transaction
    @GetMapping("/accounts/{id}/transaction/{trans_id}")
    public String getTransaction(@PathVariable Long id, @PathVariable Long trans_id, Model model){

        Account account = accountService.getAccount(id);

        model.addAttribute("users", userService.getAllUsers()); 
        model.addAttribute("transaction", transactionService.getTransaction(trans_id));
        model.addAttribute("account", account);

        return "transaction";
    }

    //Update a transaction
    @PostMapping("/accounts/{id}/transaction/{trans_id}")
    public String updateTransaction(@ModelAttribute Transaction transaction, @PathVariable Long id,
     @PathVariable Long trans_id, @RequestParam String userInput){

        User user = userService.getUserByIdNumber(Integer.parseInt(userInput));

        Transaction t = transactionService.getTransaction(trans_id);

        Account account = accountService.getAccount(id);
        int amount = 0;

        amount = account.getBalance() - t.getAmount();
        account.setBalance(amount + transaction.getAmount());
        
        accountService.updateAccount(account);

        t.setAmount(transaction.getAmount());
        t.setUser(user);

        transactionService.updateTransaction(t);

        return "redirect:/accounts/" + id + "/transaction/" + trans_id;

    }
}