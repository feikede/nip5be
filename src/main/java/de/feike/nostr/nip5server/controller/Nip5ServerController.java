package de.feike.nostr.nip5server.controller;

import de.feike.nostr.nip5server.config.Nip5ServerConfig;
import de.feike.nostr.nip5server.modell.*;
import de.feike.nostr.nip5server.service.Nip5ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class Nip5ServerController {
    final Nip5ServerService nip5ServerService;

    final Nip5ServerConfig nip5ServerConfig;

    public Nip5ServerController(Nip5ServerService nip5ServerService, Nip5ServerConfig nip5ServerConfig) {
        this.nip5ServerService = nip5ServerService;
        this.nip5ServerConfig = nip5ServerConfig;
    }

    /**
     * Give response like spec in NIP-05 of nostr protocol
     *
     * @param name The name to lookup
     * @return NIP-05 response + 200 or HTTP 404, 400
     */
    @GetMapping("/.well-known/nostr.json")
    public ResponseEntity<NostrNip05Response> getNip05(@RequestParam("name") String name) {
        try {
            return new ResponseEntity<>(nip5ServerService.getNip05Response(name), HttpStatus.OK);
        } catch (NameNotFoundException e1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadNIP05FormatException e2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Info on given name.
     *
     * @return NostrNip05Entity
     */
    @GetMapping("/nip5s-admin/info")
    public ResponseEntity<NostrNip05Entity> getNameInfo(@RequestParam("name") String name) {
        try {
            return new ResponseEntity<>(nip5ServerService.getNameInfo(name), HttpStatus.OK);
        } catch (NameNotFoundException e1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadNIP05FormatException e2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Some server stats, currently just the number of nip5 rows.
     *
     * @return NostrNip05StatsResponse
     */
    @GetMapping("/nip5s-admin/stats")
    public ResponseEntity<NostrNip05StatsResponse> getServerStats() {
        return new ResponseEntity<>(nip5ServerService.getServerStats(), HttpStatus.OK);
    }

    /**
     * Create NIP-05 rec for user, maybe secured by header secret (if set by server instance)
     *
     * @param nostrNip05CreateRequest new user record to create
     * @return 200 OK or HTTP 409, 400, 401
     */
    @PostMapping("/nip5s-admin/nostr")
    public ResponseEntity<Void> createNip05(@RequestBody NostrNip05CreateRequest nostrNip05CreateRequest) {
        try {
            nip5ServerService.createNip05(nostrNip05CreateRequest);
        } catch (NameAlreadyTakenException e1) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (BadNIP05FormatException e2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update NIP-05 rec for user, maybe secured by header secret (if set by server instance)
     *
     * @param nostrNip05UpdateRequest user record to update
     * @return 200 OK or HTTP 404, 400, 401
     */
    @PutMapping("/nip5s-admin/nostr")
    public ResponseEntity<Void> updateNip05(@RequestBody NostrNip05UpdateRequest nostrNip05UpdateRequest) {
        try {
            nip5ServerService.updateNip05(nostrNip05UpdateRequest);
        } catch (NameNotFoundException e1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadNIP05FormatException e2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
