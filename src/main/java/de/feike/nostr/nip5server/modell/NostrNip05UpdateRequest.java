package de.feike.nostr.nip5server.modell;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NostrNip05UpdateRequest {
    String name;
    String npub1;
    String[] relays = new String[]{};
    Long numSatsPaid = 0L;
    Long numSatsPayable = 0L;
    Long tsPaidUntil = 0L;
}
