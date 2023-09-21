package com.glady.challenge.usecase.model;

import com.glady.challenge.usecase.model.deposit.Deposit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "USERS")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    private Long userId;
    private String username;

    @ManyToOne
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "user")
    private List<Deposit> deposits;

}
