package com.chobbi.server.websocket;

import java.security.Principal;

/**
 * Principal for STOMP session: name = accountId so that
 * convertAndSendToUser(accountId, "queue/notifications", ...) delivers to the right client.
 */
public record WebSocketPrincipal(String name) implements Principal {

    public static WebSocketPrincipal fromAccountId(Long accountId) {
        return new WebSocketPrincipal(accountId != null ? accountId.toString() : null);
    }

    @Override
    public String getName() {
        return name;
    }
}
