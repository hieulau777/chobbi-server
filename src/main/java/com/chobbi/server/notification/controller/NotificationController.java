package com.chobbi.server.notification.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.notification.dto.NotificationDto;
import com.chobbi.server.notification.services.NotificationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServices notificationServices;

    /**
     * Lấy list thông báo cho Seller (hộp thư Seller).
     */
    @GetMapping("/seller")
    public ResponseEntity<List<NotificationDto>> getForSeller(
            @AuthenticationPrincipal AccountPrincipal principal) {
        List<NotificationDto> list = notificationServices.getForSeller(principal.getAccountId());
        return ResponseEntity.ok(list);
    }

    /**
     * Lấy list thông báo cho Buyer/Client (hộp thư Client).
     */
    @GetMapping("/client")
    public ResponseEntity<List<NotificationDto>> getForClient(
            @AuthenticationPrincipal AccountPrincipal principal) {
        List<NotificationDto> list = notificationServices.getForClient(principal.getAccountId());
        return ResponseEntity.ok(list);
    }
}
