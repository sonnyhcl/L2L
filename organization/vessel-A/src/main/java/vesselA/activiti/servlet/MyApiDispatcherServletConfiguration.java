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
package vesselA.activiti.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@ComponentScan(value = {
        "org.activiti.rest",
        "vesselA.controller"
//        "vesselpart.websocket"
})
@EnableAsync
@EnableWebMvc
public class MyApiDispatcherServletConfiguration extends WebMvcConfigurerAdapter{

    @Autowired
    protected ObjectMapper objectMapper;

    //
//	@Autowired
//	protected Environment environment;
//
    @Bean
    public SessionLocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    // Allow serving HTML files through the default Servlet  

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


//    @Bean
//    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
//        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
//        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
//        requestMappingHandlerMapping.setRemoveSemicolonContent(false);
//        requestMappingHandlerMapping.setInterceptors(getInterceptors());
//        return requestMappingHandlerMapping;
//    }
//    
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        extendMessageConverters(converters);
//        for (HttpMessageConverter<?> converter: converters) {
//            if (converter instanceof MappingJackson2HttpMessageConverter) {
//                MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter)
// converter;
//                jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//                break;
//            }
//        }
//    }
}
