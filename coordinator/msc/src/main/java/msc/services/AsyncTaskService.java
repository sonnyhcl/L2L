package msc.services;

import msc.domain.Order;
import msc.domain.SupplierPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class AsyncTaskService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);
    @Autowired
    private RestClient restClient;
    @Async
    public void  startSupplier(SupplierPart supplierPart, Order order){
        String rep = restClient.startSupplier(supplierPart, order);
        logger.info(rep);
    }
}
