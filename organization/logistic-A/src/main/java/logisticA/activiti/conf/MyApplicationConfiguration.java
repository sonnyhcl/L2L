/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package logisticA.activiti.conf;

import jxl.read.biff.BiffException;
import logisticA.domain.Freight;
import logisticA.domain.Location;
import logisticA.repos.FreightRepository;
import logisticA.repos.LocationRepository;
import logisticA.repos.MapRepository;
import logisticA.util.CsvUtil;
import logisticA.util.ExcelUtilJXL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Configuration
@PropertySources({

        @PropertySource(value = "classpath:/META-INF/activiti-app/activiti-app.properties",encoding = "UTF-8"),
//        @PropertySource("classpath:log4j.properties"),
        @PropertySource(value = "classpath:activiti-app.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:activiti-app.properties", ignoreResourceNotFound = true),

})
@ComponentScan(basePackages = {
        "logisticA.*",
        "org.activiti.app.repository",
        "org.activiti.app.service",
        "org.activiti.app.security",
        "org.activiti.app.model.component"})
public class MyApplicationConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MyApplicationConfiguration.class);

    @Inject
    private Environment environment;

    /**
     * This is needed to make property resolving work on annotations ...
     * (see http://stackoverflow.com/questions/11925952/custom-spring-property-source-does-not-resolve-placeholders-in-value)
     *
     * @Scheduled(cron="${someProperty}")
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    @Bean
    public MapRepository mapRepository(){
        String key = environment.getProperty("map.key");
        String basePath = environment.getProperty("map.basePath");
        return new MapRepository(key , basePath);
    }


    @Bean
    public LocationRepository locationRepository() throws IOException, BiffException {
        String  fileName = environment.getProperty("locations.fileName");
        logger.debug("--"+fileName+"--");
        String path = this.getClass().getResource("/").getPath()+fileName;
        List<Location> locations = CsvUtil.readLocations(path);
        logger.debug("locations : "+locations.toString());
        LocationRepository locationRepository = new LocationRepository();
        locationRepository.setLocations(locations);
        return locationRepository;
    }

    @Bean
    public FreightRepository freightRepository() throws IOException, BiffException {
        String  fileName = environment.getProperty("freightRates.fileName");
        logger.debug("--"+fileName+"--");
        String path = this.getClass().getResource("/").getPath()+fileName;
        List<Freight> freights = CsvUtil.readFreightRates(path);
        logger.debug("freights : "+freights.toString());
        FreightRepository freightRepository = new FreightRepository();
        freightRepository.setFreights(freights);
        return freightRepository;
    }



}
