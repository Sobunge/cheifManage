package com.pensasha.cheifManage.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;

    //Get all account
    public List<Account> allAccount(){
        return accountRepository.findAll();
    }

    //Get an account by id
    public Account getAccount(Long id){
        return accountRepository.findById(id).get();
    }

    //Add an account
    public Account addAccount(Account account){
        return accountRepository.save(account);
    }

    //update an account
    public Account updateAccount(Account account){
        Account existingAccount = accountRepository.findById(account.getId()).get();

        existingAccount.setDescription(account.getDescription());
        existingAccount.setName(account.getName());

        return existingAccount;
    }

    //delete an account
    public void deleteAccount(Long id){
        accountRepository.deleteById(id);
    }
}
