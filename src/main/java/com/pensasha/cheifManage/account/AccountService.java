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
    public Account getAccount(String id){
        return accountRepository.findById(id).get();
    }

    //Add an account
    public Account addAccount(Account account){
        return accountRepository.save(account);
    }

    //update an account
    public Account updateAccount(Account account){
      
      return accountRepository.save(account);
    
    }

    //delete an account
    public void deleteAccount(String id){
        accountRepository.deleteById(id);
    }

    //Getting an account by user id
    public Account getAccountByUserIdNumber(int idNumber){
        return accountRepository.findByUsersIdNumber(idNumber);
    }

    //Does account exist
    public Boolean doesAccountExist(String id){
        return accountRepository.existsById(id);
    }
}
