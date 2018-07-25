package slc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import slc.domain.Logistics;
import slc.domain.LogisticsPart;

@Service
@SuppressWarnings("all")
public class AsyncTaskService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);
    @Autowired
    private RestClient restClient;

    @Autowired
    private StompClient stompClient;

    @Async
    public void startLogistics(LogisticsPart logisticsPart, Logistics logistics){
        String rep = restClient.startLogistics(logisticsPart, logistics);
        logger.info(rep);
    }
}
