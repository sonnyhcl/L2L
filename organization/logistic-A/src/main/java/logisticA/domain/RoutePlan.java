package logisticA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutePlan {
    private Rendezvous rendezvous;
    private List<Rendezvous> rendezvousList = new ArrayList<Rendezvous>();
}
