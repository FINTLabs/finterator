package no.fintlabs.client.producers;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    private String name;
    private String note;
    private String shortDescription;
    private String orgId;

    private List<String> components = Collections.emptyList();
}
