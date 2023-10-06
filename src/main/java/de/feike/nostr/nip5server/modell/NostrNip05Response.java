package de.feike.nostr.nip5server.modell;

import java.util.Map;

public record NostrNip05Response(Map<String, String> names,
                                 Map<String, String[]> relays) {
}
