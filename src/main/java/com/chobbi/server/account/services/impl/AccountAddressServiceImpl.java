package com.chobbi.server.account.services.impl;

import com.chobbi.server.account.dto.AddressRequest;
import com.chobbi.server.account.dto.AddressResponse;
import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.account.services.AccountAddressService;
import com.chobbi.server.address.entity.AddressEntity;
import com.chobbi.server.address.repo.AddressRepo;
import com.chobbi.server.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountAddressServiceImpl implements AccountAddressService {

    private final AddressRepo addressRepo;
    private final AccountRepo accountRepo;

    @Override
    public List<AddressResponse> getMyAddresses(Long accountId) {
        List<AddressEntity> addresses =
                addressRepo.findAllByAccountEntity_IdAndDeletedAtIsNull(accountId);
        return addresses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse upsertMyAddress(Long accountId, AddressRequest request) {
        AccountEntity account = accountRepo.findById(accountId)
                .orElseThrow(() -> new BusinessException("Account not found", HttpStatus.NOT_FOUND));

        AddressEntity address;
        if (request.getId() != null) {
            address = addressRepo.findByIdAndAccountEntity_IdAndDeletedAtIsNull(
                            request.getId(),
                            account.getId()
                    )
                    .orElseThrow(() -> new BusinessException("Address not found", HttpStatus.NOT_FOUND));
        } else {
            long currentCount =
                    addressRepo.countByAccountEntity_IdAndDeletedAtIsNull(account.getId());
            if (currentCount >= 10) {
                throw new BusinessException(
                        "Bạn chỉ được tạo tối đa 10 địa chỉ nhận hàng.",
                        HttpStatus.BAD_REQUEST
                );
            }

            address = new AddressEntity();
            address.setAccountEntity(account);
            address.setIsDefault(false);
        }

        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        Boolean makeDefault = request.getIsDefault();

        if (Boolean.TRUE.equals(makeDefault)) {
            // Clear other defaults for this account
            List<AddressEntity> defaults =
                    addressRepo.findAllByAccountEntity_IdAndDeletedAtIsNullAndIsDefaultTrue(account.getId());
            for (AddressEntity other : defaults) {
                if (!other.getId().equals(address.getId())) {
                    other.setIsDefault(false);
                }
            }
            address.setIsDefault(true);
            addressRepo.saveAll(defaults);
        }

        AddressEntity saved = addressRepo.save(address);
        return toResponse(saved);
    }

    @Override
    public void deleteMyAddress(Long accountId, Long addressId) {
        AddressEntity address = addressRepo.findByIdAndAccountEntity_IdAndDeletedAtIsNull(
                        addressId,
                        accountId
                )
                .orElseThrow(() -> new BusinessException("Address not found", HttpStatus.NOT_FOUND));

        address.softDelete();
        addressRepo.save(address);
    }

    private AddressResponse toResponse(AddressEntity entity) {
        return AddressResponse.builder()
                .id(entity.getId())
                .receiverName(entity.getReceiverName())
                .phone(entity.getPhone())
                .addressLine(entity.getAddressLine())
                .ward(entity.getWard())
                .district(entity.getDistrict())
                .city(entity.getCity())
                .isDefault(entity.getIsDefault())
                .build();
    }
}

