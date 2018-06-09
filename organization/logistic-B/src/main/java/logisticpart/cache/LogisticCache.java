package logisticpart.cache;

import jxl.read.biff.BiffException;
import logisticpart.logistic.domain.WagonShadow;
import logisticpart.util.ExcelUtilJXL;
import lombok.Data;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
@Component
public class LogisticCache {
    private static final Logger logger = LoggerFactory.getLogger(LogisticCache.class);


    private final String coBasePath = "http://10.131.245.91:8080";

    private Map<String , WagonShadow> wagonShadows;
    private HashMap<String , Double> freightRates;

    public LogisticCache() throws IOException, BiffException {
        wagonShadows = new ConcurrentHashMap<String, WagonShadow>();
        //read freight rates from freights.xls
        freightRates = ExcelUtilJXL.readFreightRates(ExcelUtilJXL.getResourcePath()+"freightRates.xls" , "info");
    }
}
