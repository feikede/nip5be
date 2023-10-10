package de.feike.nostr.nip5server.repositories;

import de.feike.nostr.nip5server.modell.NostrNip05Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NostrNip05EntityRepository extends JpaRepository<NostrNip05Entity, String> {
    @Modifying
    @Query("delete from NostrNip05Entity e where e.type = 'res' and e.tsPaidUntil < :ts")
    public void deleteOutdatedReservations(@Param("ts") Long ts);

}
