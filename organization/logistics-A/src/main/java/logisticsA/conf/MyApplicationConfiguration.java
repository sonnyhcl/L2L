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
package logisticsA.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

@Configuration
@PropertySources({

        @PropertySource(value = "classpath:/META-INF/activiti-app/activiti-app.properties",encoding = "UTF-8"),
        @PropertySource(value = "classpath:activiti-app.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:activiti-app.properties", ignoreResourceNotFound = true),

})
@ComponentScan(basePackages = {
        "logisticsA.*",
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



}
