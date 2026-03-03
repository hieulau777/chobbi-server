package com.chobbi.server.auth.services;

import com.chobbi.server.auth.dto.LoginResponse;
import com.chobbi.server.auth.dto.ProviderLoginRequest;
import com.chobbi.server.auth.dto.SignUpRequest;

public interface AuthServices {
    void createAccount(SignUpRequest req);

    LoginResponse loginAsBuyer(ProviderLoginRequest req);

    LoginResponse loginAsSeller(ProviderLoginRequest req);
}
