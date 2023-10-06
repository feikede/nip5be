package de.feike.nostr.nip5server.modell;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NostrNip05CreateRequest {
    String name;
    String npub1;
    String[] relays = new String[]{};
}
