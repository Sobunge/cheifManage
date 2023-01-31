package com.pensasha.cheifManage.transaction;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.pensasha.cheifManage.role.Role;
import com.pensasha.cheifManage.user.Office;
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

        model.addAttribute("title", "Contributions");
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("users", account.getUsers());
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("user", user);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("account", account);
        model.addAttribute("accounts", accountService.allAccount());
        model.addAttribute("transactions", transactionService.getAllTransactionForAccountByStatus(id,
                com.pensasha.cheifManage.transaction.Status.ACCEPTED));

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

    // Getting pending transactions
    @GetMapping("/pendingTransaction")
    public String getAllPendingTransaction(Principal principal, Model model) {

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        model.addAttribute("title", "Pending Contributions");
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", user);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.allAccountsByStatus(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("transactions",
                transactionService.getAllTransactionByStatus(com.pensasha.cheifManage.transaction.Status.PENDING));
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

    // Getting rejected transactions
    @GetMapping("/rejectedTransaction")
    public String getAllRejectedTransactions(Principal principal, Model model) {

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        model.addAttribute("title", "Rejected Contributions");
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("user", user);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("transactions",
                transactionService.getAllTransactionByStatus(com.pensasha.cheifManage.transaction.Status.REJECTED));
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

        return "transactions";

    }

    @PostMapping("/transactionsPage")
    public RedirectView getAccountPage(Model model, Principal principal, HttpServletRequest request) {

        Account account = accountService.getAccountByName(request.getParameter("mainAccountName"));

        if (!account.getName().equals("Monthly Contribution")) {
            return new RedirectView("/accounts/" + account.getId() + "/transactions", true);
        } else {
            return new RedirectView("/accounts/" + account.getId() + "/transactions", true);
        }
    }

    @PostMapping("/monthlyTransactionPage")
    public String getMonthlyAccountPage(Model model, Principal principal, HttpServletRequest request)
            throws ParseException {

        Account account = accountService.getAccountByName("Monthly Contribution");
        int year = Integer.parseInt(request.getParameter("accountYear"));
        String month = request.getParameter("accountMonth");

        int daysInMonth = 0;
        String savedDate = "";

        Date date = new SimpleDateFormat("dd/MMMM/yyyy").parse("01/January" + "/" + year);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        savedDate = new SimpleDateFormat("yyyy-MM").format(cal.getTime());
        daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        List<Transaction> transactions = transactionService.getAllTransactionForAccountByStatus(account.getId(),
                com.pensasha.cheifManage.transaction.Status.ACCEPTED);
        List<Transaction> monthsTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            for (int i = 0; i < daysInMonth; i++) {
                if (i < 10) {
                    if (transaction.getDate().equals(savedDate + "-0" + i)) {
                        monthsTransactions.add(transaction);
                    }
                } else {
                    if (transaction.getDate().equals(savedDate + "-" + i)) {
                        monthsTransactions.add(transaction);
                    }
                }

            }
        }

        model.addAttribute("savedDate", savedDate);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("title", month);
        model.addAttribute("year", year);
        model.addAttribute("months", Month.values());
        model.addAttribute("years", yearService.getAllYears());
        model.addAttribute("users", account.getUsers());
        model.addAttribute("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));
        model.addAttribute("user", user);
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("account", account);
        model.addAttribute("accounts", accountService.allAccount());
        model.addAttribute("transactions", monthsTransactions);

        List<Message> messages = messageService.getMyUnreadMessages(Integer.parseInt(principal.getName()),
                Status.UNREAD);
        model.addAttribute("messages", messages);

        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            count++;
        }
        model.addAttribute("messageCount", count);

        return "monthlyContributions";

    }

    @PostMapping("/monthlyTransactionPagePdf")
    public ResponseEntity<?> getMonthlyAccountPagePdf(HttpServletRequest request,
            HttpServletResponse response, Principal principal) throws ParseException {

        WebContext context = new WebContext(request, response, this.servletContext);

        Account account = accountService.getAccountByName("Monthly Contribution");
        int year = Integer.parseInt(request.getParameter("accountYear"));
        String month = request.getParameter("accountMonth");

        int daysInMonth = 0;
        String savedDate = "";

        Date date = new SimpleDateFormat("dd/MMMM/yyyy").parse("01/January" + "/" + year);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        savedDate = new SimpleDateFormat("yyyy-MM").format(cal.getTime());
        daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        User user = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        List<Transaction> transactions = transactionService.getAllTransactionForAccountByStatus(account.getId(),
                com.pensasha.cheifManage.transaction.Status.ACCEPTED);
        List<Transaction> monthsTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            for (int i = 0; i < daysInMonth; i++) {
                if (i < 10) {
                    if (transaction.getDate().equals(savedDate + "-0" + i)) {
                        monthsTransactions.add(transaction);
                    }
                } else {
                    if (transaction.getDate().equals(savedDate + "-" + i)) {
                        monthsTransactions.add(transaction);
                    }
                }

            }
        }

        context.setVariable("savedDate", savedDate);
        context.setVariable("daysInMonth", daysInMonth);
        context.setVariable("title", month);
        context.setVariable("year", year);
        context.setVariable("months", Month.values());
        context.setVariable("years", yearService.getAllYears());
        context.setVariable("users", account.getUsers());
        context.setVariable("allUsers", userService.getAllActiveUsers(com.pensasha.cheifManage.user.Status.ACTIVE));
        context.setVariable("user", user);
        context.setVariable("transaction", new Transaction());
        context.setVariable("account", account);
        context.setVariable("accounts", accountService.allAccount());
        context.setVariable("transactions", monthsTransactions);

        String accountsHtml = this.templateEngine.process("reports/monthlyContributionPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(accountsHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
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
        context.setVariable("transactions", transactionService.getAllTransactionForAccountByStatus(id,
                com.pensasha.cheifManage.transaction.Status.ACCEPTED));
        context.setVariable("account", accountService.getAccount(id));

        String accountsHtml = this.templateEngine.process("reports/statementsPdf", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(baseUrl);
        HtmlConverter.convertToPdf(accountsHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object) bytes);
    }

    // Add contribution by choosing account
    @PostMapping("/addTransaction")
    public RedirectView addTransactionByChoosingAccount(HttpServletRequest request,
            @ModelAttribute Transaction transaction, RedirectAttributes redit, Principal principal) {

        String redirectView;

        Account account = accountService.getAccount(request.getParameter("accountId"));
        User activeUser = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        if (transactionService.doesTransactionWithReferenceNumberExist(transaction.getReferenceNumber())) {
            redit.addFlashAttribute("transactionFail", "Transaction with a similer reference number already exists");
            redirectView = "/accounts/" + account.getId() + "/transactions";
        } else {

            account.setBalance(account.getBalance() + transaction.getAmount());

            SimpleDateFormat dateFormater = new SimpleDateFormat("YYYY-MM-dd");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Nairobi/Kenya"));
            try {
                calendar.setTime(dateFormater.parse(transaction.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

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

            transaction.setUser(userService.getUserByIdNumber(Integer.parseInt(request.getParameter("idNumberInput"))));
            transaction.setAccount(account);
            transaction.setYear(year);

            if (activeUser.getOffice().equals(Office.SECRETARY) || activeUser.getOffice().equals(Office.TREASURER)
                    || activeUser.getRole().equals(Role.SUPER_ADMIN)) {

                transaction.setStatus(com.pensasha.cheifManage.transaction.Status.ACCEPTED);
                transactionService.addTransaction(transaction);

                redit.addFlashAttribute("transactionSuccess", "Transaction successfully recorded");
                redirectView = "/accounts/" + account.getId() + "/transactions";
            } else {

                transaction.setStatus(com.pensasha.cheifManage.transaction.Status.PENDING);
                transactionService.addTransaction(transaction);

                redit.addFlashAttribute("transactionSuccess", "Transaction successfully recorded, pending approval");
                redirectView = "/pendingTransaction";
            }

        }

        return new RedirectView(redirectView, true);

    }

    // Adding a transaction
    @PostMapping("/accounts/{accountId}/transaction")
    public RedirectView addTransaction(@PathVariable String accountId, @ModelAttribute Transaction transaction,
            Principal principal, RedirectAttributes redit,
            @RequestParam int idNumberInput) {

        String redirectView;
        User activeUser = userService.getUserByIdNumber(Integer.parseInt(principal.getName()));

        if (transactionService.doesTransactionWithReferenceNumberExist(transaction.getReferenceNumber())) {
            redit.addFlashAttribute("transactionFail", "Transaction with a similer reference number already exists");
            redirectView = "/accounts/" + accountId + "/transactions";
        } else {

            Account account = accountService.getAccount(accountId);
            account.setBalance(account.getBalance() + transaction.getAmount());

            SimpleDateFormat dateFormater = new SimpleDateFormat("YYYY-MM-dd");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Nairobi/Kenya"));
            try {
                calendar.setTime(dateFormater.parse(transaction.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

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

            if (activeUser.getOffice().equals(Office.SECRETARY) || activeUser.getOffice().equals(Office.TREASURER)
                    || activeUser.getRole().equals(Role.SUPER_ADMIN)) {

                transaction.setStatus(com.pensasha.cheifManage.transaction.Status.ACCEPTED);
                transactionService.addTransaction(transaction);

                redit.addFlashAttribute("transactionSuccess", "Transaction successfully recorded");
                redirectView = "/accounts/" + account.getId() + "/transactions";
            } else {

                transaction.setStatus(com.pensasha.cheifManage.transaction.Status.PENDING);
                transactionService.addTransaction(transaction);

                redit.addFlashAttribute("transactionSuccess", "Transaction successfully recorded, pending approval");
                redirectView = "/pendingTransaction";
            }

        }

        return new RedirectView(redirectView, true);

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
        context.setVariable("transactions", transactionService.getAllTransactionForAccountByStatus(id,
                com.pensasha.cheifManage.transaction.Status.ACCEPTED));
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