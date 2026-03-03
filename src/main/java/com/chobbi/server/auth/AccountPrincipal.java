package com.chobbi.server.auth;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.entity.AccountRolesEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Principal dùng cho Spring Security sau khi JWT filter xác thực thành công.
 * Cho phép controller lấy accountId qua @AuthenticationPrincipal.
 */
@Getter
public class AccountPrincipal implements UserDetails {

    private final Long accountId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AccountPrincipal(AccountEntity account) {
        this.accountId = account.getId();
        this.email = account.getEmail();
        this.authorities = account.getAccountRoles() == null
                ? Collections.emptyList()
                : account.getAccountRoles().stream()
                        .map(AccountRolesEntity::getRolesEntity)
                        .filter(r -> r != null && r.getRole() != null)
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRole().name()))
                        .collect(Collectors.toList());
    }

    public Long getAccountId() {
        return accountId;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
