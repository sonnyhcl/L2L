package iot.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
public class CommonRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int defautDelayHour = 6; //默认港口停留时间
    private int zoomInVal = 1000; // 如果按1000的压缩比，停留一小时只需要3.6s

    public CommonRepository(){
        logger.debug("--"+defautDelayHour+"--"+zoomInVal);
    }
}
