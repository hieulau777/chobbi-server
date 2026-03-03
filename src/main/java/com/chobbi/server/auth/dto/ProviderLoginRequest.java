package com.chobbi.server.auth.dto;

import com.chobbi.server.auth.enums.AuthProviderEnums;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProviderLoginRequest {
    private String email;
    private String name;
    private AuthProviderEnums provider;
    private String providerAccountId;
}
