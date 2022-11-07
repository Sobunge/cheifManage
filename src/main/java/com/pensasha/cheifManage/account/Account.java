package com.pensasha.cheifManage.account;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.cheifManage.transaction.Transaction;
import com.pensasha.cheifManage.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @NotNull
    private int id;

    @Column(length = 15)
    @Size(min = 2, max = 15)
    @NotNull
    private String name;

    @Column(length = 254)
    @NotNull
    private String description;

    @NotNull
    private int balance = 0;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<Transaction> transactions;

    @OneToOne
    private User user;
    
}