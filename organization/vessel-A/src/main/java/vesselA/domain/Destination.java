package vesselA.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Destination {
    private String name;
    private String estiAnchorTime;
    private String estiArrivalTime;
    private String estiDepartureTime;
}
