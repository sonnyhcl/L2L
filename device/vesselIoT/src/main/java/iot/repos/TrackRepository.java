package iot.repos;

import iot.domain.*;
import iot.util.CsvUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class TrackRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrackRepository.class);
    private List<Track> tracks = new ArrayList<Track>();

    public TrackRepository(@Value("${awsiot.keys}") String keysCsv , LocationRepository locationRepository) throws IOException {
        String dataPath = this.getClass().getResource("/").getPath()+ "data/";
        logger.debug("root path : "+dataPath);
        List<AwsKey> awsKeys = CsvUtil.readAwsKeys(dataPath+keysCsv);
        //Construct tracks
        for(int i = 0; i < awsKeys.size(); i++){
            Track t = new Track();
            String vid = awsKeys.get(i).getVid();
            List<String> destinations = CsvUtil.readDestinations(dataPath+"DE"+vid+".csv");
            List<VesselState> vesselStates = CsvUtil.readTracjectory(dataPath+"VS"+vid+".csv");
            //split into steps
            int len = vesselStates.size();
            int k = 0;
            int dSize = destinations.size();
            Location loc = locationRepository.findLocation(destinations.get(k));
            Step step = new Step();
            step.setPrePort("起始点");
            List<VesselState> stepVesselStates = new ArrayList<VesselState>();
            step.setVesselStates(stepVesselStates);
            List<Step> steps = new ArrayList<Step>();
            if(k < dSize){
                step.setNextPort(loc.getName());
                steps.add(step);
                for(int j = 0 ; j < len ;j++){
                    VesselState vs = vesselStates.get(j);
                    steps.get(k).getVesselStates().add(vs);
                    if(vs.getLatitude() == loc.getLatitude() && vs.getLongitude() == loc.getLongitude()){
                        if(k >= dSize-1){
                            break;
                        }

                        //new step
                        step = new Step();
                        step.setPrePort(loc.getName());
                        stepVesselStates = new ArrayList<VesselState>();
                        step.setVesselStates(stepVesselStates);

                        k++;
                        loc = locationRepository.findLocation(destinations.get(k));
                        step.setNextPort(loc.getName());
                        steps.add(step);
                    }
                }
            }
            t.setVid(vid);
            t.setDestinations(destinations);
            t.setSteps(steps);
            save(t);
        }
        logger.debug("tracks");

    }

    public  void save(Track track){
        tracks.add(track);
    }

    public  Track findTrack(String vid){
        for(Track t : tracks){
            if(vid.equals(t.getVid())){
                return t;
            }
        }
        return null;
    }
}
