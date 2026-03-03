package com.chobbi.server.shop.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.shop.dto.ShopResponse;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shop.repo.ShopRepo;
import com.chobbi.server.shop.services.ShopServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopServicesImpl implements ShopServices {

    private final ShopRepo shopRepo;
    private final AccountRepo accountRepo;

    @Override
    public Optional<ShopResponse> getMyShop(Long accountId) {
        return shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .map(this::toResponse);
    }

    @Override
    public ShopResponse createShop(Long accountId, String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Tên shop không được để trống", HttpStatus.BAD_REQUEST);
        }

        if (shopRepo.existsByAccountEntity_Id(accountId)) {
            throw new BusinessException("Tài khoản đã có shop", HttpStatus.BAD_REQUEST);
        }

        AccountEntity account = accountRepo.findById(accountId)
                .orElseThrow(() -> new BusinessException("Account not found", HttpStatus.NOT_FOUND));

        ShopEntity shop = new ShopEntity();
        shop.setName(name.trim());
        shop.setAccountEntity(account);

        ShopEntity saved = shopRepo.save(shop);
        return toResponse(saved);
    }

    private ShopResponse toResponse(ShopEntity shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .status(shop.getStatus())
                .build();
    }
}

