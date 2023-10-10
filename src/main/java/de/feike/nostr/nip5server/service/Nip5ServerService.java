package de.feike.nostr.nip5server.service;

import de.feike.nostr.nip5server.controller.*;
import de.feike.nostr.nip5server.modell.*;
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
    public NostrNip05Response getNip05Response(String name) throws NameNotFoundException, BadNIP05FormatException, NameReservedException {
        var entity = getNip05Entity(name);
        log.debug("Response lookup: " + name);
        if ("res".equals(entity.getType())) {
            throw new NameReservedException();
        }
        HashMap<String, String> names = new HashMap<>();
        names.put(name, entity.getHexpub());
        // TODO: Add some default relays until we need real answers
        HashMap<String, String[]> relays = new HashMap<>();
        relays.put(entity.getHexpub(),
                new String[]{"wss://nos.lol", "wss://eden.nostr.land"});

        // we don't support relays for now
        return new NostrNip05Response(names, null);
    }

    @Transactional(readOnly = true)
    public NostrNip05StatsResponse getServerStats() {
        Long cnt = nostrNip05EntityRepository.count();
        return new NostrNip05StatsResponse(cnt);
    }

    @Transactional
    public void createNip05(NostrNip05CreateRequest nostrNip05CreateRequest) throws NameAlreadyTakenException, BadNIP05FormatException, BadRecTypeException {
        if (!NostrNip05Entity.isValidType(nostrNip05CreateRequest.getType())) {
            throw new BadRecTypeException();
        }
        String sanitized = sanitizeNIP5Name(nostrNip05CreateRequest.getName());
        if (null == sanitized) {
            throw new BadNIP05FormatException();
        }
        var check = nostrNip05EntityRepository.findById(sanitized);
        if (check.isPresent()) {
            log.debug("Name already taken: " + sanitized);
            throw new NameAlreadyTakenException();
        }
        log.info("Creating NIP-05 for Name: " + sanitized);
        NostrNip05Entity nostrNip05Entity = new NostrNip05Entity();
        nostrNip05Entity.setName(sanitized);
        nostrNip05Entity.setType(nostrNip05CreateRequest.getType());
        nostrNip05Entity.setHexpub(nostrNip05CreateRequest.getHexpub());
        nostrNip05EntityRepository.save(nostrNip05Entity);
    }

    @Transactional
    public void updateNip05(NostrNip05UpdateRequest nostrNip05UpdateRequest) throws NameNotFoundException, BadNIP05FormatException, BadRecTypeException {
        if (!NostrNip05Entity.isValidType(nostrNip05UpdateRequest.getType())) {
            throw new BadRecTypeException();
        }
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
        nostrNip05Entity.setHexpub(nostrNip05UpdateRequest.getHexpub());
        nostrNip05Entity.setNumSatsPayable(nostrNip05UpdateRequest.getNumSatsPayable());
        nostrNip05Entity.setTsPaidUntil(nostrNip05UpdateRequest.getTsPaidUntil());
        nostrNip05Entity.setType(nostrNip05UpdateRequest.getType());
        nostrNip05EntityRepository.save(nostrNip05Entity);
    }

    public NostrNip05Entity getNameInfo(String name) throws NameNotFoundException, BadNIP05FormatException {
        return getNip05Entity(name);
    }

}
