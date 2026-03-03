package com.chobbi.server.account.services;


import com.chobbi.server.account.dto.AddressRequest;
import com.chobbi.server.account.dto.AddressResponse;

import java.util.List;

public interface AccountAddressService {

    List<AddressResponse> getMyAddresses(Long accountId);

    AddressResponse upsertMyAddress(Long accountId, AddressRequest request);

    void deleteMyAddress(Long accountId, Long addressId);
}

