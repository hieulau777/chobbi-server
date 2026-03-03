package com.chobbi.server.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    private Long id;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String phone;

    @NotBlank
    private String addressLine;

    @NotBlank
    private String ward;

    @NotBlank
    private String district;

    @NotBlank
    private String city;

    private Boolean isDefault;
}

