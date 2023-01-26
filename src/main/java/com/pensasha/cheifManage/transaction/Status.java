package com.pensasha.cheifManage.transaction;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {

    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    String status;
}
