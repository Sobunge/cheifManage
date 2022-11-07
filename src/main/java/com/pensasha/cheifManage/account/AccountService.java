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
    public Account getAccount(Integer id){
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
    public void deleteAccount(int id){
        accountRepository.deleteById(id);
    }

    //Getting an account by user id
    public Account getAccountByUserIdNumber(int idNumber){
        return accountRepository.findByUserIdNumber(idNumber);
    }

    //Does account exist
    public Boolean doesAccountExist(int id){
        return accountRepository.existsById(id);
    }
}
