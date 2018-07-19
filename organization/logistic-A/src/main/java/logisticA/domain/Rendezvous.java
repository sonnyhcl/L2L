package logisticA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rendezvous {
    private String name;
    private Path route;
    private long trafficThreshold;
    private double sumCost;
}
