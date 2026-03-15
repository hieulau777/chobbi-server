package com.chobbi.server.auth;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Bảo vệ các API /admin/** bằng cách kiểm tra email + mật khẩu (pwd) gửi kèm
 * trong header mỗi request. Dùng riêng cho chobbi-admin, không dùng JWT.
 */
@Component
@RequiredArgsConstructor
public class AdminPasswordFilter extends OncePerRequestFilter {

    private final AccountRepo accountRepo;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) return true;
        // Bỏ qua các endpoint login admin (nếu có) và non-admin path
        if (!path.startsWith("/admin/")) return true;
        return path.startsWith("/admin/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String email = request.getHeader("X-Admin-Email");
        String pwd = request.getHeader("X-Admin-Pwd");

        if (email == null || email.isBlank() || pwd == null || pwd.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Vui lòng đăng nhập admin.\"}");
            return;
        }

        // Chỉ cho phép đúng email admin cấu hình
        if (!"hieulau0112@gmail.com".equalsIgnoreCase(email)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Bạn không có quyền truy cập Chobbi Admin.\"}");
            return;
        }

        Optional<AccountEntity> accountOpt = accountRepo.findByEmail(email);
        if (accountOpt.isEmpty() || accountOpt.get().getPwd() == null || !pwd.equals(accountOpt.get().getPwd())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Sai thông tin đăng nhập admin.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

