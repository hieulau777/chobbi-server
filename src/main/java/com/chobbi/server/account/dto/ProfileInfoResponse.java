package com.chobbi.server.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileInfoResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final String avatarUrl;
}

