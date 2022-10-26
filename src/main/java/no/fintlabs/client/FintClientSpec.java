package no.fintlabs.client;

import lombok.*;
import no.fintlabs.FintSpec;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FintClientSpec implements FintSpec {

    private String orgId;
    private String note;
    private String shortDescription;
    private List<String> components = Collections.emptyList();

}
