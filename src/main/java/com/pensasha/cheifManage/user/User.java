package com.pensasha.cheifManage.user;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.cheifManage.message.Message;
import com.pensasha.cheifManage.role.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotNull
    @Column(length = 15)
    @Size(min = 2, max = 15)
    private String firstName, thirdName;

    @Column(length = 15)
    private String secondName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Id
    @Column(length = 8)
    private int idNumber;

    private String email;

    private String residentialAddress;

    private String county;

    private String division;

    private String location;

    private String subLocation;

    private Title title;

    @Column(length = 10)
    private int phoneNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Office office;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    @ManyToMany(mappedBy = "recievers", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Collection<Message> messages;

    @OneToMany(mappedBy = "sender", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Collection<Message> sms;
}
