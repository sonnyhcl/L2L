package iot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AwsKey {
    private String vid;
    private String thingName;
    private String localPath;
    private String clientEndpoint;
    private String defaultTopic;
    private String customTopic;
}
