package de.feike.nostr.nip5server.service;

import de.feike.nostr.nip5server.repositories.NostrNip05EntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
public class CronService {


    private final NostrNip05EntityRepository nostrNip05EntityRepository;

    public CronService(NostrNip05EntityRepository nostrNip05EntityRepository) {
        this.nostrNip05EntityRepository = nostrNip05EntityRepository;
    }

    /*
    Cleanup reservation every 3 mins
     */
    @Transactional
    @Scheduled(fixedDelay = 180000L, initialDelay = 30000L)
    public void cleanupReservations() {
        log.debug("running cleanupReservations()");
        nostrNip05EntityRepository.deleteOutdatedReservations(Instant.now().getEpochSecond());
    }

    /*
    Cleanup expired sales
     */
    @Transactional
    @Scheduled(fixedDelay = 600000L, initialDelay = 180000L)
    public void cleanupExpiredSales() {
        log.debug("running cleanupExpiredSales()");
        nostrNip05EntityRepository.deleteExpiredBookings(Instant.now().getEpochSecond());
    }

}
