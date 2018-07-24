package logisticsA.repos;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Data
public class MapRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${map.key}")
    private String key;

    @Value("${map.basePath}")
    private String basePath;

    @Autowired
    private RestTemplate restTemplate;

    public MapRepository(String key,String basePath){
        //logger.info(key+"--"+basePath);
        this.key = key;
        this.basePath = basePath;
        logger.debug("map repos");
    }

    public String PlanPath(String x1, String y1, String x2, String y2){
        Map<String , Object> uriVars = new HashMap<String , Object>();
//        uriVars.put("width" , 2.5);
        uriVars.put("strategy" , 0);
        uriVars.put("extensions" ," base");
//        uriVars.put("weight" , 10);

        uriVars.put("origin" , x1+","+y1);
        uriVars.put("destination" , x2+","+y2);
        uriVars.put("output" , "json");
        uriVars.put("key" , key);
        String url = basePath+"?strategy={strategy}&extensions={extensions}&origin={origin}&destination={destination}&output={output}&key={key}";
        String pathInfo = restTemplate.getForObject(url , String.class, uriVars);
//        logger.debug("path info "+ pathInfo);
        return pathInfo;

    }
}
