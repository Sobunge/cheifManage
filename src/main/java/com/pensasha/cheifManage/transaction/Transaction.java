package com.pensasha.cheifManage.transaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.pensasha.cheifManage.account.Account;
import com.pensasha.cheifManage.month.Month;
import com.pensasha.cheifManage.user.User;
import com.pensasha.cheifManage.year.Year;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(value = 0)
    private int amount = 0;
    
    @NotNull
    @Column(length = 8)
    private String time;
    
    @NotNull 
    @Column(length = 13)
    private String date;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "account_id", nullable = true)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "year")
    private Year year;

    @Enumerated(EnumType.STRING)
    private Month month;

    @NotNull
    private String referenceNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}