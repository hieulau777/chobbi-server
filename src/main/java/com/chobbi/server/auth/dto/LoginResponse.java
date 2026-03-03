package com.chobbi.server.auth.dto;

import com.chobbi.server.account.enums.RoleEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Long accountId;
    private String email;
    private String name;
    private List<RoleEnums> roles;
    private Boolean hasShop;

    public LoginResponse(String token, Long accountId, String email, String name, List<RoleEnums> roles, Boolean hasShop) {
        this.token = token;
        this.accountId = accountId;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.hasShop = hasShop;
    }
}
