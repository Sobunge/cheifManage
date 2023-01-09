package com.pensasha.cheifManage.message;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.cheifManage.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Message {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_sender")
    private User sender;

    private String sendersName;

    @JsonIgnore
    @OrderColumn
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "user_messages", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "id_number"))
    private List<User> recievers;
    private String subject;
    private String mail;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Date date;
    private Status status;

}
