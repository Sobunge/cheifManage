package com.pensasha.cheifManage.role;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    
    ACCOUNTS_MANAGER("Accounts_manager"),
    SUPER_ADMIN("Super_admin"),
    COUNTY_ADMIN("County_admin"),
    USER("user");

    String role;

}  