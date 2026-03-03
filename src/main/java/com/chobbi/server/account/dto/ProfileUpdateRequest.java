package com.chobbi.server.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank
    private String name;

    private String phone;

    /** Nếu true, xóa ảnh đại diện trên server. */
    private Boolean removeAvatar;
}

