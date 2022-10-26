package no.fintlabs.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FintClient {
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String orgId;
}
