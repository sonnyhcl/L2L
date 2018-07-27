package vmc.domain;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {
    public List<String> Destinations2Dnames(List<Destination> dests){
        List<String> dNames = new ArrayList<String>();
        for(Destination destination : dests){
                dNames.add(destination.getName());
        }
        return dNames;
    }
}
