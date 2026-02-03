package com.chobbi.server.cart.repo;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Long, CartEntity> {

}
