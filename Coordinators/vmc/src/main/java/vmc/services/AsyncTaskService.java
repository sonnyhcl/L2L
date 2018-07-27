package vmc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vmc.domain.Application;
import vmc.domain.VesselPart;

@Service
@SuppressWarnings("all")
public class AsyncTaskService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);


    @Autowired
    private RestClient restClient;

    @Async
    public void startManager(String url , Application application , VesselPart vesselPart){
        restClient.startManager(url , application , vesselPart);
    }


}
