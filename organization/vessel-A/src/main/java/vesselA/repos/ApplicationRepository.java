package vesselA.repos;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vesselA.domain.Application;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class ApplicationRepository {
    private  static final Logger logger = LoggerFactory.getLogger(ApplicationRepository.class);

    private List<Application> applications = new ArrayList<Application>();

    public void save(Application application){
        applications.add(application);
    }

    public Application findById(String applyId){
        for(Application application : applications){
            if(applyId.equals(application.getId())){
                return application;
            }
        }
        return null;
    }
}
