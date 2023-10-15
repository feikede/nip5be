package de.feike.nostr.nip5server.modell;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NostrNip05CreateRequest {
    String name;
    String hexpub;
    String type;
    String[] relays;
    Long numSatsPaid;
    Long numSatsPayable;
    Long tsPaidUntil;

    public NostrNip05CreateRequest(String name, String hexpub, String type, String[] relays, Long numSatsPaid, Long numSatsPayable, Long tsPaidUntil) {
        this.name = name;
        this.hexpub = hexpub;
        this.type = type;
        this.relays = null != relays ? relays : new String[]{};
        this.numSatsPaid = null != numSatsPaid ? numSatsPaid : 0L;
        this.numSatsPayable = null != numSatsPayable ? numSatsPayable : 0L;
        this.tsPaidUntil = null != tsPaidUntil ? tsPaidUntil : 0L;
    }
}
