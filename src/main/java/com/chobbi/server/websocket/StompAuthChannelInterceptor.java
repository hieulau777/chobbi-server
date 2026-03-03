package com.chobbi.server.websocket;

import com.chobbi.server.auth.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * Intercepts STOMP CONNECT: reads JWT from header (Authorization or token),
 * validates and sets user principal with name = accountId for user-destination routing.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        if (token == null || token.isEmpty()) {
            token = accessor.getFirstNativeHeader("token");
        }

        if (token != null && !token.isEmpty()) {
            try {
                Long accountId = jwtService.getAccountIdFromToken(token);
                if (accountId != null) {
                    accessor.setUser(WebSocketPrincipal.fromAccountId(accountId));
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // Invalid/expired token — leave user unset; connection may be rejected
            }
        }

        return message;
    }
}
