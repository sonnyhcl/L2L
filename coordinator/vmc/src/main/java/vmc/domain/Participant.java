package vmc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    protected  String orgName;
    @NonNull protected String orgId;
    @NonNull protected String host;
    @NonNull protected String port;
    @NonNull protected String projectId;

    public String getUrl(){
        return "http://"+this.host+":"+this.port+"/"+projectId;
    }
}
