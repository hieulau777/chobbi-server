package com.chobbi.server.auth.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.entity.AccountRolesEntity;
import com.chobbi.server.account.entity.RolesEntity;
import com.chobbi.server.account.entity.SocialAccountEntity;
import com.chobbi.server.account.enums.RoleEnums;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.account.repo.AccountRolesRepo;
import com.chobbi.server.account.repo.RolesRepo;
import com.chobbi.server.account.repo.SocialAccountRepo;
import com.chobbi.server.auth.JwtService;
import com.chobbi.server.auth.dto.LoginResponse;
import com.chobbi.server.auth.dto.ProviderLoginRequest;
import com.chobbi.server.auth.dto.SignUpRequest;
import com.chobbi.server.auth.services.AuthServices;
import com.chobbi.server.auth.services.EmailBlacklistService;
import com.chobbi.server.shop.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import com.chobbi.server.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServicesImpl implements AuthServices {

    private final SocialAccountRepo socialAccountRepo;
    private final AccountRepo accountRepo;
    private final RolesRepo rolesRepo;
    private final AccountRolesRepo accountRolesRepo;
    private final ShopRepo shopRepo;
    private final JwtService jwtService;
    private final EmailBlacklistService emailBlacklistService;

    @Override
    public void createAccount(SignUpRequest req) {

    }

    @Override
    public LoginResponse loginAsBuyer(ProviderLoginRequest req) {
        if (emailBlacklistService.isBlacklisted(req.getEmail())) {
            throw new BusinessException("Tài khoản của bạn đã bị khóa.", HttpStatus.FORBIDDEN);
        }
        Optional<SocialAccountEntity> existingSocialAccount = socialAccountRepo
                .findByProviderAndProviderAccountId(req.getProvider(), req.getProviderAccountId());

        if (existingSocialAccount.isPresent()) {
            AccountEntity account = existingSocialAccount.get().getAccountEntity();
            return buildLoginResponse(account.getId());
        }

        String name = (req.getName() != null && !req.getName().isBlank())
                ? req.getName()
                : (req.getEmail() != null && req.getEmail().contains("@")
                        ? req.getEmail().substring(0, req.getEmail().indexOf("@"))
                        : "User");

        AccountEntity account = new AccountEntity();
        account.setEmail(req.getEmail());
        account.setName(name);
        account.setPwd(null);
        accountRepo.save(account);

        RolesEntity buyerRole = rolesRepo.findByRole(RoleEnums.BUYER)
                .orElseThrow(() -> new RuntimeException("BUYER role not found"));

        AccountRolesEntity accountRole = new AccountRolesEntity();
        accountRole.setAccountEntity(account);
        accountRole.setRolesEntity(buyerRole);
        accountRolesRepo.save(accountRole);

        SocialAccountEntity socialAccount = new SocialAccountEntity();
        socialAccount.setAccountEntity(account);
        socialAccount.setProvider(req.getProvider());
        socialAccount.setProviderAccountId(req.getProviderAccountId());
        socialAccountRepo.save(socialAccount);

        return buildLoginResponse(account.getId());
    }

    @Override
    public LoginResponse loginAsSeller(ProviderLoginRequest req) {
        if (emailBlacklistService.isBlacklisted(req.getEmail())) {
            throw new BusinessException("Tài khoản của bạn đã bị khóa.", HttpStatus.FORBIDDEN);
        }
        Optional<SocialAccountEntity> existingSocialAccount = socialAccountRepo
                .findByProviderAndProviderAccountId(req.getProvider(), req.getProviderAccountId());

        if (existingSocialAccount.isPresent()) {
            AccountEntity account = existingSocialAccount.get().getAccountEntity();
            ensureSellerRole(account);
            return buildLoginResponse(account.getId());
        }

        String name = (req.getName() != null && !req.getName().isBlank())
                ? req.getName()
                : (req.getEmail() != null && req.getEmail().contains("@")
                        ? req.getEmail().substring(0, req.getEmail().indexOf("@"))
                        : "User");

        AccountEntity account = new AccountEntity();
        account.setEmail(req.getEmail());
        account.setName(name);
        account.setPwd(null);
        accountRepo.save(account);

        RolesEntity buyerRole = rolesRepo.findByRole(RoleEnums.BUYER)
                .orElseThrow(() -> new RuntimeException("BUYER role not found"));

        RolesEntity sellerRole = rolesRepo.findByRole(RoleEnums.SELLER)
                .orElseThrow(() -> new RuntimeException("SELLER role not found"));

        AccountRolesEntity accountRoleBuyer = new AccountRolesEntity();
        accountRoleBuyer.setAccountEntity(account);
        accountRoleBuyer.setRolesEntity(buyerRole);
        accountRolesRepo.save(accountRoleBuyer);

        AccountRolesEntity accountRoleSeller = new AccountRolesEntity();
        accountRoleSeller.setAccountEntity(account);
        accountRoleSeller.setRolesEntity(sellerRole);
        accountRolesRepo.save(accountRoleSeller);

        SocialAccountEntity socialAccount = new SocialAccountEntity();
        socialAccount.setAccountEntity(account);
        socialAccount.setProvider(req.getProvider());
        socialAccount.setProviderAccountId(req.getProviderAccountId());
        socialAccountRepo.save(socialAccount);

        return buildLoginResponse(account.getId());
    }

    private LoginResponse buildLoginResponse(Long accountId) {
        AccountEntity account = accountRepo.findByIdWithRoles(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        String token = jwtService.generateToken(account);
        List<RoleEnums> roles = account.getAccountRoles().stream()
                .map(ar -> ar.getRolesEntity().getRole())
                .collect(Collectors.toList());
        boolean hasShop = shopRepo.existsByAccountEntity_Id(account.getId());
        return new LoginResponse(
                token,
                account.getId(),
                account.getEmail(),
                account.getName(),
                roles,
                hasShop
        );
    }

    private void ensureSellerRole(AccountEntity account) {
        boolean hasSellerRole = account.getAccountRoles().stream()
                .anyMatch(ar -> RoleEnums.SELLER.equals(ar.getRolesEntity().getRole()));
        if (!hasSellerRole) {
            RolesEntity sellerRole = rolesRepo.findByRole(RoleEnums.SELLER)
                    .orElseThrow(() -> new RuntimeException("SELLER role not found"));
            AccountRolesEntity accountRole = new AccountRolesEntity();
            accountRole.setAccountEntity(account);
            accountRole.setRolesEntity(sellerRole);
            accountRolesRepo.save(accountRole);
        }
    }
}
