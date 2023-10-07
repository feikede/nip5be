package de.feike.nostr.nip5server;

import de.feike.nostr.nip5server.controller.BadNIP05FormatException;
import de.feike.nostr.nip5server.controller.NameAlreadyTakenException;
import de.feike.nostr.nip5server.controller.NameNotFoundException;
import de.feike.nostr.nip5server.modell.NostrNip05CreateRequest;
import de.feike.nostr.nip5server.modell.NostrNip05UpdateRequest;
import de.feike.nostr.nip5server.service.Nip5ServerService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("junit")
class Nip5serverApplicationTests {

    @Autowired
    private Nip5ServerService nip5ServerService;

    @Test
    void sanitizeNIP05Name() {
        assertNull(nip5ServerService.sanitizeNIP5Name(null));
        assertNull(nip5ServerService.sanitizeNIP5Name(""));
        assertNull(nip5ServerService.sanitizeNIP5Name("j#u8"));
        assertNull(nip5ServerService.sanitizeNIP5Name("o~p"));
        assertNull(nip5ServerService.sanitizeNIP5Name("x p"));
        assertEquals("rainer", nip5ServerService.sanitizeNIP5Name("Rainer"));
        assertEquals("rainer", nip5ServerService.sanitizeNIP5Name("rainer"));
    }

    @Test
    void createNip() throws NameAlreadyTakenException, NameNotFoundException, BadNIP05FormatException {
        String npub = RandomStringUtils.randomAlphanumeric(64);
        // create nip
        nip5ServerService.createNip05(
                NostrNip05CreateRequest.builder().name("createNip")
                        .npub1(npub)
                        .relays(new String[]{"one", "two"}).build()
        );
        // retry must fail
        assertThrows(NameAlreadyTakenException.class, () ->
                nip5ServerService.createNip05(
                        NostrNip05CreateRequest.builder().name("createNip")
                                .npub1(npub)
                                .relays(new String[]{"one", "two"}).build()
                ));

        var e = nip5ServerService.getNip05Entity("createNip");
        Assertions.assertEquals(npub, e.getNpub1());
    }

    @Test
    void updateNip() throws NameAlreadyTakenException, NameNotFoundException, BadNIP05FormatException {
        String npub = RandomStringUtils.randomAlphanumeric(64);
        // create nip
        nip5ServerService.createNip05(
                NostrNip05CreateRequest.builder().name("updateNip")
                        .npub1(npub)
                        .relays(new String[]{"one", "two"}).build()
        );
        // update undef must fail
        assertThrows(NameNotFoundException.class, () ->
                nip5ServerService.updateNip05(
                        NostrNip05UpdateRequest.builder().name("undef")
                                .npub1(npub)
                                .numSatsPaid(20L)
                                .numSatsPayable(1L)
                                .tsPaidUntil(Instant.now().getEpochSecond() + 48 * 3600)
                                .build()
                ));
        nip5ServerService.updateNip05(
                NostrNip05UpdateRequest.builder().name("updateNip")
                        .npub1(npub)
                        .numSatsPaid(20L)
                        .numSatsPayable(1L)
                        .tsPaidUntil(Instant.now().getEpochSecond() + 48 * 3600)
                        .build()
        );

        var e = nip5ServerService.getNip05Entity("updateNip");
        Assertions.assertEquals(npub, e.getNpub1());
        Assertions.assertEquals(20L, e.getNumSatsPaid());
    }

}
