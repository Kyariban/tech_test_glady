package com.glady.challenge.usecase.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Company  {
    @Id
    private Long companyId;
    private String companyName;
    private BigDecimal balance;

    @OneToMany(mappedBy = "company")
    private List<User> users;
}
