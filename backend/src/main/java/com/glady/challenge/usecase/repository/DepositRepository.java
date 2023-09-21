package com.glady.challenge.usecase.repository;

import com.glady.challenge.usecase.model.deposit.Deposit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends CrudRepository<Deposit,Long> {
    List<Deposit> findAllByUser_UserId(Long userId);
}
