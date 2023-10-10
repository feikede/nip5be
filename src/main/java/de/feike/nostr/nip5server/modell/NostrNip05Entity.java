package de.feike.nostr.nip5server.modell;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "nostr_nip05entity", indexes = {
        @Index(name = "i_tsPaidUntil", columnList = "ts_paid_until"),
        @Index(name = "i_type", columnList = "type"),
})
public class NostrNip05Entity {

    @Id
    private String name;

    // hex32 public key - naming in DB :-(
    @Column(nullable = false, name = "npub1")
    private String hexpub;

    // Unix secs timestamp of creation
    @Column(nullable = false)
    private Long tsCreation;

    // Unix secs timestamp of last prolongation (set to 0 for first purchase)
    @Column(nullable = false)
    private Long tsLastProlongation = 0L;

    // number of prolongations that user already purchased
    @Column(nullable = false)
    private Long tsNumProlongations = 0L;

    // paid amount in sats
    @Column(nullable = false)
    private Long numSatsPaid = 0L;

    // current promised price
    @Column(nullable = false)
    private Long numSatsPayable = 0L;

    // paid until that date
    @Column(nullable = false, name = "ts_paid_until")
    private Long tsPaidUntil = 0L;

    // currently of [sale, res, free]
    // res means reservation -> will not be delivered on nostr.json?name=x but is taken till "tsPaidUnitil"
    @Column(nullable = false, name = "type")
    private String type = "sale";

    @PrePersist
    protected void onCreate() {
        tsCreation = Instant.now().getEpochSecond();
    }

    public static boolean isValidType(String type) {
        if (null == type) return false;
        return switch (type) {
            case "sale", "res", "free" -> true;
            default -> false;
        };
    }

}
