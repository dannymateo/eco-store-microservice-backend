package com.itm.api_gateway.messaging;

import tools.jackson.databind.JsonNode;

public record NatsResponse(
        boolean success,
        JsonNode data,
        String error
) {
}
