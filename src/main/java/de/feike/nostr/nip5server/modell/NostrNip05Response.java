package de.feike.nostr.nip5server.modell;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

public record NostrNip05Response(
        Map<String, String> names,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, String[]> relays) {
}
