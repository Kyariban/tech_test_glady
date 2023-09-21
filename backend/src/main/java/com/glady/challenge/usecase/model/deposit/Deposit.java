package com.glady.challenge.usecase.model.deposit;

import com.glady.challenge.usecase.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DEPOSIT_TYPE")
@SuperBuilder
@Data
public abstract class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    private Long companyId;
    private BigDecimal amount;
    private Date expirationDate;

    protected Deposit(BigDecimal amount, Long companyId, User user, Date date) {
        this.amount = amount;
        this.companyId = companyId;
        this.user = user;
        this.expirationDate = defineExpirationDate(date);
    }

    protected Deposit() {

    }


    protected abstract Date defineExpirationDate(Date date);
}
