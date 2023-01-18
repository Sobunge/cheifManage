package com.pensasha.cheifManage.account;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.JoinColumn;
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
    @Column(length = 19)
    private String id;

    @Column(length = 70)
    @Size(min = 2, max = 70)
    @NotNull
    private String name;

    @Column(length = 254)
    @NotNull
    private String description;

    @NotNull
    private int balance = 0;

    private int minimumBalanace = 0;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<Transaction> transactions;

    @JsonIgnore
    @OrderColumn
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "user_accounts", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "id_number"))
    private List<User> users;
    
}
