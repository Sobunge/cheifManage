package com.pensasha.cheifManage.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private String firstName, secondName, thirdName;

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

}
