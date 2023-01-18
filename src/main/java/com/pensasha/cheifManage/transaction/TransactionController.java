package com.pensasha.cheifManage.transaction;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.account.AccountService;
import com.pensasha.cheifManage.message.Status;
import com.pensasha.cheifManage.message.Message;
import com.pensasha.cheifManage.message.MessageService;
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

    @Autowired
    MessageService messageService;

    @Autowired
    private ServletContext servletContext;

    // private final String baseUrl = "http://localhost:8081/";
    private final String baseUrl = "https://national-chief.azurewebsites.net/";

    private final TemplateEngine templateEngine;

    public TransactionController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    // Getting account transactions
    @GetMapping("/accounts/{id}/transactions")
    public String getAccountTransactions(@PathVariable String id, Principal principal, Model model) {

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        Account account = accountService.getAccount(id);

        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("users", account.getUsers());
        model.addAttribute("user", user);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("account", account);
        model.addAttribute("accounts", accountService.allAccount());
        model.addAttribute("transactions", transactionService.getAllTransactionForAccount(id));
        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "transactions";
    }

    @PostMapping("/transactions")
    public RedirectView getAccountPdf(Model model, Principal principal, HttpServletRequest request) {

        Account account = accountService.getAccountByName(request.getParameter("accountName"));

        if (!account.getName().equals("Monthly Contribution")) {
            return new RedirectView("/accounts/" + account.getId() + "/transactions/pdf", true);
        } else {
            return new RedirectView("/accounts/" + account.getId() + "/transactions/pdf", true);
        }

    }

    // Getting transactions pdf
    @GetMapping("/accounts/{id}/transactions/pdf")
    public ResponseEntity<?> getTransactionsPdf(@PathVariable String id, HttpServletRequest request,
            HttpServletResponse response, Principal principal) {

        WebContext context = new WebContext(request, response, this.servletContext);

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));
        Account account = accountService.getAccount(id);

        context.setVariable("users", account.getUsers());
        context.setVariable("user", user);
        context.setVariable("transaction", new Transaction());
        context.setVariable("transactions", transactionService.getAllTransactionForAccount(id));
        context.setVariable("account", accountService.getAccount(id));

        String accountsHtml = this.templateEngine.process("reports/statementsPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(accountsHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    // Adding a transaction
    @PostMapping("/accounts/{accountId}/transaction")
    public RedirectView addTransaction(@PathVariable String accountId, @ModelAttribute Transaction transaction,
            Principal principal, RedirectAttributes redit,
            @RequestParam int idNumberInput) {

        if (transactionService.doesTransactionWithReferenceNumberExist(transaction.getReferenceNumber())) {
            redit.addFlashAttribute("transactionFail", "Transaction with a similer reference number already exists");
        } else {

            Account account = accountService.getAccount(accountId);
            account.setBalance(account.getBalance() + transaction.getAmount());

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
                    transaction.setMonth(Month.NOVEMBER);
                    break;
                default:
                    months.add(Month.DECEMBER);
                    transaction.setMonth(Month.DECEMBER);
            }

            year.setMonths(months);
            yearService.saveYear(year);

            transaction.setUser(userService.getUserByIdNumber(idNumberInput));
            transaction.setAccount(account);
            transaction.setYear(year);
            transaction.setDate(dFormat.format(date));
            transaction.setTime(tFormat.format(date));

            transactionService.addTransaction(transaction);

            redit.addFlashAttribute("transactionSuccess", "Transaction successfully recorded");
        }

        return new RedirectView("/accounts/" + accountId + "/transactions", true);

    }

    // Deleting a transaction
    @GetMapping("/accounts/{id}/transactions/{trans_id}")
    public RedirectView deleteTransactionFromAccount(@PathVariable String id, @PathVariable Long trans_id,
            RedirectAttributes redit) {

        if (transactionService.doesTransactionExist(trans_id)) {

            Account account = accountService.getAccount(id);
            Transaction transaction = transactionService.getTransaction(trans_id);
            account.setBalance(account.getBalance() - transaction.getAmount());

            accountService.updateAccount(account);

            transactionService.deleteTransaction(trans_id);

            redit.addFlashAttribute("transactionSuccess", "Transaction successfully deleted");

        } else {

            redit.addFlashAttribute("transactionFail", "Transaction does not exist");
        }

        return new RedirectView("/accounts/" + id + "/transactions", true);
    }

    // Getting a transaction
    @GetMapping("/accounts/{id}/transaction/{trans_id}")
    @ResponseBody
    public String getTransaction(@PathVariable String id, @PathVariable Long trans_id, Model model,
            Principal principal) {

        return "Hello World";
        /*
         * Account account = accountService.getAccount(id);
         * 
         * model.addAttribute("user",
         * userService.getUserByIdNumber(Integer.parseInt(principal.getName())));
         * model.addAttribute("users", userService.getAllUsers());
         * model.addAttribute("transaction",
         * transactionService.getTransaction(trans_id));
         * model.addAttribute("account", account);
         * List<Message> messages =
         * messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
         * Status.UNREAD);
         * model.addAttribute("messages", messages);
         * 
         * int count = 0;
         * for (int i = 0; i < messages.size(); i++) {
         * count++;
         * }
         * model.addAttribute("messageCount", count);
         * 
         * return "transaction";
         */

    }

    // Getting a transaction pdf
    @GetMapping("/accounts/{id}/transaction/{trans_id}/pdf")
    public ResponseEntity<?> getTransactionReciept(@PathVariable String id, @PathVariable Long trans_id,
            HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("transactions", transactionService.getAllTransactionForAccount(id));
        context.setVariable("account", accountService.getAccount(id));
        context.setVariable("transaction", transactionService.getTransaction(trans_id));

        String transactionHtml = this.templateEngine.process("reports/transactionPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(transactionHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    // Update a transaction
    @PostMapping("/accounts/{id}/transaction/{trans_id}")
    public RedirectView updateTransaction(@ModelAttribute Transaction transaction, @PathVariable String id,
            @PathVariable Long trans_id, @RequestParam String userInput, RedirectAttributes redit) {

        if (transactionService.doesTransactionExist(trans_id)) {
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

            redit.addFlashAttribute("transactionSuccess", "Transaction Successfully Updated");
        } else {
            redit.addFlashAttribute("transactionFail", "Transaction does not exist");
        }

        return new RedirectView("/accounts/" + id + "/transactions", true);

    }
}