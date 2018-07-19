package logisticA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutePlan {
    private String id;
    private String lpid;
    private String msgType;
    private HashMap<String , Object> msgBody = new HashMap<String , Object>();
    private Rendezvous rendezvous;
    private List<Rendezvous> rendezvousList = new ArrayList<Rendezvous>();
    private int sequence;
}
