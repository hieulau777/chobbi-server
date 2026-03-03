package com.chobbi.server.auth.controller;

import com.chobbi.server.auth.dto.LoginResponse;
import com.chobbi.server.auth.dto.LogoutResponse;
import com.chobbi.server.auth.dto.ProviderLoginRequest;
import com.chobbi.server.auth.dto.SignUpRequest;
import com.chobbi.server.auth.services.AuthServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthServices authServices;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest req) {
        authServices.createAccount(req);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/google/buyer")
    public ResponseEntity<LoginResponse> loginAsBuyer(@RequestBody ProviderLoginRequest req) {
        LoginResponse response = authServices.loginAsBuyer(req);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google/seller")
    public ResponseEntity<LoginResponse> loginAsSeller(@RequestBody ProviderLoginRequest req) {
        LoginResponse response = authServices.loginAsSeller(req);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        // JWT đang dùng stateless nên server không giữ session; client chỉ cần xóa token.
        return ResponseEntity.ok(new LogoutResponse("Đăng xuất thành công."));
    }
}
