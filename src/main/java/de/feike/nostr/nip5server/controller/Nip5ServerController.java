package de.feike.nostr.nip5server.controller;

import de.feike.nostr.nip5server.modell.NostrNip05CreateRequest;
import de.feike.nostr.nip5server.modell.NostrNip05Response;
import de.feike.nostr.nip5server.modell.NostrNip05UpdateRequest;
import de.feike.nostr.nip5server.service.Nip5ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class Nip5ServerController {

    Nip5ServerService nip5ServerService;

    public Nip5ServerController(Nip5ServerService nip5ServerService) {
        this.nip5ServerService = nip5ServerService;
    }

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

    @PostMapping("/.well-known/nostr.json")
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

    @PutMapping("/.well-known/nostr.json")
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
