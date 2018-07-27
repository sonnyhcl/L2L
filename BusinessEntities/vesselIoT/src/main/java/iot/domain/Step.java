package iot.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Step {
    private List<VesselState> vesselStates;
    private String prePort;
    private String nextPort;
}
