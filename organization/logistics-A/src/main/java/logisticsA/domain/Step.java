package logisticsA.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Step {
    private long distance;
    private long duration;
    private List<Point> polyline = new ArrayList<Point>();
}
