package no.fintlabs.client;

import lombok.*;
import no.fintlabs.FlaisSpec;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FintClientSpec implements FlaisSpec {

    private String orgId;
    private String note;
    private String shortDescription;
    private List<String> components = Collections.emptyList();

}
