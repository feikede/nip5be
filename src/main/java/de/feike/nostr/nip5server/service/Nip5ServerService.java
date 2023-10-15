package de.feike.nostr.nip5server.service;

import de.feike.nostr.nip5server.controller.*;
import de.feike.nostr.nip5server.modell.NostrNip05CreateRequest;
import de.feike.nostr.nip5server.modell.NostrNip05Entity;
import de.feike.nostr.nip5server.modell.NostrNip05Response;
import de.feike.nostr.nip5server.modell.NostrNip05StatsResponse;
import de.feike.nostr.nip5server.repositories.NostrNip05EntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public NostrNip05Entity getNip05EntityByName(String name) throws NameNotFoundException, BadNIP05FormatException {
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
        var entity = getNip05EntityByName(name);
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
        nostrNip05Entity.setNumSatsPaid(nostrNip05CreateRequest.getNumSatsPaid());
        nostrNip05Entity.setNumSatsPayable(nostrNip05CreateRequest.getNumSatsPayable());
        nostrNip05Entity.setTsPaidUntil(nostrNip05CreateRequest.getTsPaidUntil());
        nostrNip05EntityRepository.save(nostrNip05Entity);
    }

    @Transactional
    public void updateNip05(NostrNip05CreateRequest nostrNip05UpdateRequest) throws NameNotFoundException, BadNIP05FormatException, BadRecTypeException {
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

    @Transactional(readOnly = true)
    public NostrNip05Entity getNip05Info(String name, String hexpub) throws NameNotFoundException, BadNIP05FormatException {
        if (StringUtils.hasLength(name)) {
            return getNip05EntityByName(name);
        } else if (StringUtils.hasLength(hexpub) && (hexpub.length() == 64)) {
            var list = nostrNip05EntityRepository.findAllByHexpub(hexpub);
            if (list.isEmpty()) {
                throw new NameNotFoundException();
            }
            return list.get(0);
        }
        throw new BadNIP05FormatException();
    }

    public void deleteNip05(String name) throws NameNotFoundException, BadNIP05FormatException {
        var e = getNip05EntityByName(name);
        nostrNip05EntityRepository.delete(e);
    }
}
