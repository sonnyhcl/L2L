package vesseldevice.services.pubSub;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import org.apache.log4j.Logger;

/**
 * This class extends {@link AWSIotMessage} to provide customized handlers for
 * non-blocking message publishing.
 *
 * @Author bqzhu
 */
public class VesselPublisher  extends AWSIotMessage {
    private  static Logger logger = Logger.getLogger(VesselPublisher.class);

    public VesselPublisher(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
    }

    @Override
    public void onFailure() {
        super.onFailure();
        logger.debug(topic+" : fail");
    }

    @Override
    public void onTimeout() {
        super.onTimeout();
        logger.debug(topic+" : timeout");
    }
}
