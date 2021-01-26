package model;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Restriction {

    private Integer facilityId;

    @Builder.Default
    private Set<String> bannedStates = new HashSet<>();

    private Float maxDefaultLikelyhood;
}
