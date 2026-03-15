package com.chobbi.server.auth;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.auth.services.EmailBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter xác thực JWT: đọc Authorization: Bearer, verify token, lấy email,
 * tìm account và set Authentication vào SecurityContext để phân quyền.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AccountRepo accountRepo;
    private final EmailBlacklistService emailBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String token = extractBearerToken(authorization);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtService.getEmailFromToken(token);
                if (emailBlacklistService.isBlacklisted(email)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"Tài khoản của bạn đã bị khóa.\"}");
                    return;
                }
                Optional<AccountEntity> accountOpt = accountRepo.findByEmailWithRoles(email);
                if (accountOpt.isPresent()) {
                    AccountPrincipal principal = new AccountPrincipal(accountOpt.get());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // Token không hợp lệ / hết hạn → không set authentication, request sẽ là unauthenticated
            }
        }

        filterChain.doFilter(request, response);
    }

    private static String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String t = authorization.substring(7).trim();
        return t.isEmpty() ? null : t;
    }
}
