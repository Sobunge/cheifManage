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

    //Get an account by name
    public Account getAccountByName(String name){
        return accountRepository.findByName(name);
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

    //Get accounts by a user
    public List<Account> getUsersAccounts(int idNumber){
        return accountRepository.findAllByUsersIdNumber(idNumber);
    }

    //Does account exist
    public Boolean doesAccountExist(String id){
        return accountRepository.existsById(id);
    }

    //Does account exist by name
    public Boolean doesAccountExistByName(String name){
        return accountRepository.existsByName(name);
    }
}
