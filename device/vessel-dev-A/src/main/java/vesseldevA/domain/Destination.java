package vesseldevA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destination {
    private String name;
    private String estiAnchorTime;
    private String estiArrivalTime;
    private String estiDepartureTime;

}
