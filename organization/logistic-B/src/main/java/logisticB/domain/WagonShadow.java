package logisticB.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WagonShadow {
    private String id;
    private double longitude;
    private double latitude;
    private double velocity;

    private String wpid;
    private String rendezvous;
    private List<Port> candidatePorts;
}
