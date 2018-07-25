package iot.repos;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
@SuppressWarnings("all")
public class MapRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${map.key}")
    private String key;

    @Value("${map.basePath}")
    private String basePath;

    @Autowired
    private RestTemplate restTemplate;


    MapRepository(){
        //logger.info(key+"--"+basePath);
        logger.debug("map repos");
    }

    public String PlanPath(String x1, String y1, String x2, String y2){
        logger.info(restTemplate.toString());
        Map<String , Object> uriVars = new HashMap<String , Object>();
        uriVars.put("width" , 2.5);
        uriVars.put("strategy" , 2);
        uriVars.put("size" ,2);
        uriVars.put("weight" , 10);
        uriVars.put("origin" , x1+","+y1);
        uriVars.put("destination" , x2+","+y2);
        uriVars.put("output" , "json");
        uriVars.put("key" , key);
        String url = basePath+"?width={width}&strategy={strategy}&size={size}&weight" +
                "={weight}&origin={origin}&destination={destination}&output={output}&key={key}";
        String pathInfo = restTemplate.getForObject(url , String.class, uriVars);
        return pathInfo;

    }
}
