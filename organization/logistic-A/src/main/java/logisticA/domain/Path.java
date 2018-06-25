package logisticA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Path {
    private Point origin;
    private Point destination;
    private long distance;
    private long duration;
    private List<Point> polyline = new ArrayList<Point>();
}
