package vmc.domain;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Destination {
    private String name;
    private String estiArrivalTime;
    private String estiAnchorTime;
    private String estiDepartureTime;
}
