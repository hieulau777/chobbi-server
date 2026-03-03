package com.chobbi.server.account.controller;

import com.chobbi.server.account.dto.ProfileInfoResponse;
import com.chobbi.server.account.dto.ProfileUpdateRequest;
import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.account.services.AccountAddressService;
import com.chobbi.server.account.dto.AddressRequest;
import com.chobbi.server.account.dto.AddressResponse;
import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.storage.FolderTypeEnum;
import com.chobbi.server.storage.services.FilesStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private static final long MAX_AVATAR_SIZE_BYTES = 10L * 1024 * 1024;

    private final AccountRepo accountRepo;
    private final FilesStorageService filesStorageService;
    private final AccountAddressService accountAddressService;

    @GetMapping("/info")
    public ResponseEntity<ProfileInfoResponse> getProfileInfo(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        AccountEntity account = accountRepo.findById(principal.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        ProfileInfoResponse response = new ProfileInfoResponse(
                account.getId(),
                account.getEmail(),
                account.getName(),
                account.getPhone(),
                account.getAvatarUrl()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/info",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProfileInfoResponse> updateProfile(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestPart("profile") @Valid ProfileUpdateRequest profileRequest,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        AccountEntity account = accountRepo.findById(principal.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setName(profileRequest.getName());
        account.setPhone(profileRequest.getPhone());

        if (Boolean.TRUE.equals(profileRequest.getRemoveAvatar())) {
            account.setAvatarUrl(null);
        } else if (avatar != null && !avatar.isEmpty()) {
            if (avatar.getSize() > MAX_AVATAR_SIZE_BYTES) {
                throw new BusinessException("Avatar size must be <= 10MB", HttpStatus.BAD_REQUEST);
            }

            String avatarPath = filesStorageService.transferStorage(avatar, FolderTypeEnum.AVATARS);
            account.setAvatarUrl(avatarPath);
            filesStorageService.processOptimization(avatarPath);
        }

        accountRepo.save(account);

        ProfileInfoResponse response = new ProfileInfoResponse(
                account.getId(),
                account.getEmail(),
                account.getName(),
                account.getPhone(),
                account.getAvatarUrl()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/address")
    public ResponseEntity<?> getMyAddresses(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        List<AddressResponse> addresses =
                accountAddressService.getMyAddresses(principal.getAccountId());
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/address")
    public ResponseEntity<AddressResponse> upsertMyAddress(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse response =
                accountAddressService.upsertMyAddress(principal.getAccountId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<Void> deleteMyAddress(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id
    ) {
        accountAddressService.deleteMyAddress(principal.getAccountId(), id);
        return ResponseEntity.noContent().build();
    }
}

