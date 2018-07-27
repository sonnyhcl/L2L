package vesselA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IoTSetting {
    private String id;
    private String defaultTopic;
    private String customTopic;
}
