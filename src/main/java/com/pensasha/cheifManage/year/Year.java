package com.pensasha.cheifManage.year;


import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.cheifManage.month.Month;
import com.pensasha.cheifManage.transaction.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Year {
    
    @Id
    @Min(value = 2018)
    @Max(value = 2030)
    private short year;

    @JsonIgnore                                     
    @Column(length = 9)
    @ElementCollection(targetClass = Month.class)
    @CollectionTable(name = "year_months", joinColumns = @JoinColumn(name = "year"))
    @Enumerated(EnumType.STRING)
    private Set<Month> months;

    @JsonIgnore
    @OneToMany(mappedBy = "year", cascade = {CascadeType.ALL})
    private Collection<Transaction> transactions;
    
}