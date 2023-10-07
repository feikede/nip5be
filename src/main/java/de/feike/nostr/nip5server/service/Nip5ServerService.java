package de.feike.nostr.nip5server.service;

import de.feike.nostr.nip5server.controller.BadNIP05FormatException;
import de.feike.nostr.nip5server.controller.NameAlreadyTakenException;
import de.feike.nostr.nip5server.controller.NameNotFoundException;
import de.feike.nostr.nip5server.modell.NostrNip05CreateRequest;
import de.feike.nostr.nip5server.modell.NostrNip05Entity;
import de.feike.nostr.nip5server.modell.NostrNip05Response;
import de.feike.nostr.nip5server.modell.NostrNip05UpdateRequest;
import de.feike.nostr.nip5server.repositories.NostrNip05EntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Slf4j
@Service
public class Nip5ServerService {

    private final NostrNip05EntityRepository nostrNip05EntityRepository;

    public Nip5ServerService(NostrNip05EntityRepository nostrNip05EntityRepository) {
        this.nostrNip05EntityRepository = nostrNip05EntityRepository;
    }

    /**
     * Apply NIP-05 character rules
     *
     * @param name - the name to check
     * @return the sanitized name or NULL on char violation
     */
    public String sanitizeNIP5Name(String name) {
        if ((null == name) || (name.isEmpty())) {
            return null;
        }
        String sanitized = name.toLowerCase();
        if (!sanitized.matches("^[-a-z0-9._]+")) {
            return null;
        }
        return sanitized;
    }

    @Transactional(readOnly = true)
    public NostrNip05Entity getNip05Entity(String name) throws NameNotFoundException, BadNIP05FormatException {
        String sanitized = sanitizeNIP5Name(name);
        if (null == sanitized) {
            throw new BadNIP05FormatException();
        }
        log.debug("Name lookup: " + sanitized);
        var check = nostrNip05EntityRepository.findById(sanitized);
        if (check.isEmpty()) {
            throw new NameNotFoundException();
        }
        return check.get();
    }

    @Transactional(readOnly = true)
    public NostrNip05Response getNip05Response(String name) throws NameNotFoundException, BadNIP05FormatException {
        var entity = getNip05Entity(name);
        log.debug("Response lookup: " + name);
        HashMap<String, String> names = new HashMap<>();
        names.put(name, entity.getNpub1());
        // TODO: Add some default relays until we need real answers
        HashMap<String, String[]> relays = new HashMap<>();
        relays.put(entity.getNpub1(),
                new String[]{"wss://nos.lol", "wss://eden.nostr.land"});

        // we don't support relays for now
        return new NostrNip05Response(names, null);
    }

    @Transactional
    public void createNip05(NostrNip05CreateRequest nostrNip05CreateRequest) throws NameAlreadyTakenException, BadNIP05FormatException {
        String sanitized = sanitizeNIP5Name(nostrNip05CreateRequest.getName());
        if (null == sanitized) {
            throw new BadNIP05FormatException();
        }
        var check = nostrNip05EntityRepository.findById(sanitized);
        if (check.isPresent()) {
            log.debug("Name already taken: " + sanitized);
            throw new NameAlreadyTakenException();
        }
        log.info("Creating for Name: " + sanitized);
        NostrNip05Entity nostrNip05Entity = new NostrNip05Entity();
        nostrNip05Entity.setName(sanitized);
        nostrNip05Entity.setNpub1(nostrNip05CreateRequest.getNpub1());
        nostrNip05EntityRepository.save(nostrNip05Entity);
    }

    @Transactional
    public void updateNip05(NostrNip05UpdateRequest nostrNip05UpdateRequest) throws NameNotFoundException, BadNIP05FormatException {
        String sanitized = sanitizeNIP5Name(nostrNip05UpdateRequest.getName());
        if (null == sanitized) {
            throw new BadNIP05FormatException();
        }
        var check = nostrNip05EntityRepository.findById(sanitized);
        if (check.isEmpty()) {
            throw new NameNotFoundException();
        }
        NostrNip05Entity nostrNip05Entity = check.get();
        nostrNip05Entity.setNumSatsPaid(nostrNip05UpdateRequest.getNumSatsPaid());
        nostrNip05Entity.setNumSatsPayable(nostrNip05UpdateRequest.getNumSatsPayable());
        nostrNip05Entity.setTsPaidUntil(nostrNip05Entity.getTsPaidUntil());
        nostrNip05EntityRepository.save(nostrNip05Entity);
    }
}
