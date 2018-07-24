package vesseldevA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AwsKey {
    private String vid;
    private String localPath;
    private String clientEndpoint;
}
