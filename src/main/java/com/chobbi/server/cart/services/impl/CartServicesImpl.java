package com.chobbi.server.cart.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.cart.dto.AddCartRequest;
import com.chobbi.server.cart.entity.CartEntity;
import com.chobbi.server.cart.repo.CartRepo;
import com.chobbi.server.cart.services.CartServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServicesImpl implements CartServices {
    private final CartRepo cartRepo;

    @Override
    public void addToCart(AddCartRequest req) {

    }

//    private CartEntity getCartOrCreate(Long accountId) {
//
//    }
}
