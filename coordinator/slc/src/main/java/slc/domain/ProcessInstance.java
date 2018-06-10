package slc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstance {
    @NonNull private String id;
    @NonNull private String orgId; // organization dependency.
}
