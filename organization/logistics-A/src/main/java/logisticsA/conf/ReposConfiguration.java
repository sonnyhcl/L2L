package logisticsA.conf;

import jxl.read.biff.BiffException;
import logisticsA.domain.Freight;
import logisticsA.domain.Location;
import logisticsA.repos.CommonRepository;
import logisticsA.repos.FreightRepository;
import logisticsA.repos.LocationRepository;
import logisticsA.repos.MapRepository;
import logisticsA.util.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Configuration
@SuppressWarnings("all")
public class ReposConfiguration {
    private  final static Logger logger = LoggerFactory.getLogger(ReposConfiguration.class);
    @Inject
    private Environment environment;
//    @Bean
    public CommonRepository commonRepository(){
        CommonRepository commonRepository = new CommonRepository(environment);
        return  commonRepository;
    }
    @Bean
    public LocationRepository locationRepository() throws IOException {
        String  fileName = environment.getProperty("locations.fileName");
        logger.debug("--"+fileName+"--");
        String path = this.getClass().getResource("/").getPath()+"data/"+fileName;
        List<Location> locations = CsvUtil.readLocations(path);
        logger.debug("locations : "+locations.toString());
        LocationRepository locationRepository = new LocationRepository();
        locationRepository.setLocations(locations);
        return locationRepository;
    }

    @Bean
    public MapRepository mapRepository(){
        String key = environment.getProperty("map.key");
        String basePath = environment.getProperty("map.basePath");
        return new MapRepository(key , basePath);
    }

    @Bean
    public FreightRepository freightRepository() throws IOException, BiffException {
        String  fileName = environment.getProperty("freightRates.fileName");
        logger.debug("--"+fileName+"--");
        String path = this.getClass().getResource("/").getPath()+"data/"+fileName;
        List<Freight> freights = CsvUtil.readFreightRates(path);
        logger.debug("freights : "+freights.toString());
        FreightRepository freightRepository = new FreightRepository();
        freightRepository.setFreights(freights);
        return freightRepository;
    }
}
