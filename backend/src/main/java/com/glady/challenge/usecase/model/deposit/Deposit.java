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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long depositId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private final User user;

    private final Long companyId;
    private final BigDecimal amount;
    private final Date expirationDate;

    protected Deposit(BigDecimal amount, Long companyId, User user) {
        this.amount = amount;
        this.companyId = companyId;
        this.user = user;
        this.expirationDate = defineExpirationDate();
    }


    protected abstract Date defineExpirationDate();
}
