package com.pensasha.cheifManage.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {
    
    ACTIVE("Active"),
    INACTIVE("Inactive");

    String status;
}
