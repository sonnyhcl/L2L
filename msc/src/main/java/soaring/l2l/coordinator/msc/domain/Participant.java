package soaring.l2l.coordinator.msc.domain;

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
    @NonNull protected String pid;
    @NonNull protected String url;


    protected static String getUrl(){
        String url = null;
        url = "http://10.131.245.91";
        return url;
    }
}
