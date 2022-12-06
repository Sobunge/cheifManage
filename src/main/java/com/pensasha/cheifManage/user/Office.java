package com.pensasha.cheifManage.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Office {
    
    CHAIRMAN("Chairman"),
    SECRETARY("Secretary"),
    TREASURER("Treasurer"),
    DELEGATES("Delegates"),
    MEMBER("Member");

    String office;
}
