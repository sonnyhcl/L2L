package lvc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstance {
    @NonNull protected String id;
    @NonNull protected String orgId; // organization dependency.
}
