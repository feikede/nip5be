package de.feike.nostr.nip5server;

/*
 * Server application for the NIP-05 nostr protocol
 * https://github.com/nostr-protocol/nips/blob/master/05.md
 * <p>
 * Provided as is, without any warranty or rights.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot Server Application
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Nip5serverApplication {

    public static void main(String[] args) {
        SpringApplication.run(Nip5serverApplication.class, args);
    }

}
