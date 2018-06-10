package managerA.repos;

import lombok.Data;
import managerA.domain.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Data
@Component
public class ApplicationRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

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
