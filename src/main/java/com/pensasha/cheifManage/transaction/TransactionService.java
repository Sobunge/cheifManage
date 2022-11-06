package com.pensasha.cheifManage.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    //Getting all transaction for an account
    public List<Transaction> getAllTransactionForAccount(Long accountId){
        return transactionRepository.findAllByAccountId(accountId);
    }

    //Get a transaction
    public Transaction getTransaction(Long transactionId){
        return transactionRepository.findById(transactionId).get();
    }

    //Adding a transaction
    public Transaction addTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    //Updating a transaction
    public Transaction updateTransaction(Transaction transaction){
        Transaction existingTransaction = transactionRepository.findById(transaction.getId()).get();

        existingTransaction.setAmount(transaction.getAmount());

        return transactionRepository.save(existingTransaction);
    }

    //Deleting a transaction
    public void deleteTransaction(Long id){
        transactionRepository.deleteById(id); 
    }
}
