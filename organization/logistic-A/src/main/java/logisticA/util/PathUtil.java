package logisticA.util;


import com.fasterxml.jackson.databind.JsonNode;
import logisticA.domain.Path;
import logisticA.domain.Point;
import logisticA.repos.MapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PathUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MapRepository mapRepository;

    public  static Path extractPath(JsonNode pathNode){
        Path path = new Path();
        JsonNode route = pathNode.get("data").get("route");
        String destStr = route.findValue("destination").asText();
        String origStr = route.findValue("origin").asText();
        JsonNode path0 = route.get("paths").get(0);
        long dist = path0.findValue("distance").asLong();
        long dura = path0.findValue("duration").asLong();
        path.setDestination(extractPoint(destStr));
        path.setOrigin(extractPoint(origStr));
        path.setDistance(dist);
        path.setDuration(dura);
        JsonNode steps = path0.get("steps");
        int len = steps.size();
        List<Point> pointList = new ArrayList<Point>();
        for(int i = 0 ; i < len; i++){
            JsonNode step = steps.get(i);
            String polyline = step.findValue("polyline").asText();
            if(i == len-1){
                extractPolyline(pointList , polyline , 0 , 0);//[0 , len-1]
            }else{
                extractPolyline(pointList , polyline , 0 , 1);//[0 , len]
            }
        }
        path.setPolyline(pointList);

        return path;
    }


    public  static  Point extractPoint(String s){
        Point point = new Point();
        String[] xy = s.split(",");
        double lng = Double.parseDouble(xy[0]);
        double lat = Double.parseDouble(xy[1]);
        point.setLongitude(lng);
        point.setLatitude(lat);
        return point;
    }
    public static void extractPolyline(List<Point> pointList , String s , int left , int right){
        String[] points = s.split(";");
        int len = points.length - right;
        for(int i = left ; i < len; i++){
            Point point = extractPoint(points[i]);
            pointList.add(point);
        }
    }

}
