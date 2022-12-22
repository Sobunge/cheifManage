package com.pensasha.cheifManage.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
   
    List<Transaction> findAllByAccountId(String accountId);
    Boolean existsByReferenceNumber(String refNumber);
    List<Transaction> findAllByUserIdNumber(int idNumber);
}
