package de.feike.nostr.nip5server.modell;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class NostrNip05Entity {

    @Id
    private String name;

    // npub key
    @Column(nullable = false)
    private String npub1;

    // Unix secs timestamp of creation
    @Column(nullable = false)
    private Long tsCreation;

    // Unix secs timestamp of last prologation (set to 0 for first purchase)
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
    @Column(nullable = false)
    private Long tsPaidUntil = 0L;

    @PrePersist
    protected void onCreate() {
        tsCreation = Instant.now().getEpochSecond();
    }
}
