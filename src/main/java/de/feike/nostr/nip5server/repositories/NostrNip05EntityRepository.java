package de.feike.nostr.nip5server.repositories;

import de.feike.nostr.nip5server.modell.NostrNip05Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NostrNip05EntityRepository extends JpaRepository<NostrNip05Entity, String> {
}
