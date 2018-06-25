package wagonA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rendezvous {
    private String name;
    private String route;
    private double freightCharge;
}
