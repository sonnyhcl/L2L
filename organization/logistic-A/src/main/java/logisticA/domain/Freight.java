package logisticA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.Rate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Freight {
    private String name;
    private double freightRate;
}
