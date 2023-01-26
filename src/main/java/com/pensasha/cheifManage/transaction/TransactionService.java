package com.pensasha.cheifManage.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    //Getting all transaction for a user
    public List<Transaction> getAllUserTransaction(int idNumber){
        return transactionRepository.findAllByUserIdNumber(idNumber);
    }

    //Getting all transaction for an account
    public List<Transaction> getAllTransactionForAccount(String accountId){
        return transactionRepository.findAllByAccountId(accountId);
    } 

    //Getting all transaction for account and status
    public List<Transaction> getAllTransactionForAccountByStatus(String accountId, Status status){
        return transactionRepository.findAllByAccountIdAndStatus(accountId, status);
    }

    //Getting all transaction by status
    public List<Transaction> getAllTransactionByStatus(Status status){
        return transactionRepository.findAllByStatus(status);
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

    //Does transaction exist
    public Boolean doesTransactionExist(Long id){
        return transactionRepository.existsById(id);
    }

    //Does an account with a similer reference number exist
    public Boolean doesTransactionWithReferenceNumberExist(String refNumber){
        return transactionRepository.existsByReferenceNumber(refNumber);
    }
}
