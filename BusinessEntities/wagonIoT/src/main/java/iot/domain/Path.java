package iot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Path {
    private Point origin;
    private Point destination;
    private long distance;
    private long duration;
    private List<Point> polyline;
}
