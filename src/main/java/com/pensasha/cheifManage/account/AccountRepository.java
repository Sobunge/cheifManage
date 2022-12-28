package com.pensasha.cheifManage.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String>{
    
    Account findByUsersIdNumber(int idNumber);
}
