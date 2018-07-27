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
    @Value("${wagon.wid}")
    private String wid;
    @Value("${wagon.zoomInVal}")
    private int zoomInVal;

}
