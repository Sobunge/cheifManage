package com.pensasha.cheifManage.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String>{
    
    Boolean existsByName(String name);

    Account findByName(String name);

    List<Account> findAllByUsersIdNumber(int idNumber);
}
