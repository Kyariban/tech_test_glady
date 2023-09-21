package com.glady.challenge.usecase.repository;

import com.glady.challenge.usecase.model.deposit.Deposit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends CrudRepository<Deposit,Long> {
}
