package de.feike.nostr.nip5server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "nip5-server")
public class Nip5ServerConfig {
    String adminSecret;
}
