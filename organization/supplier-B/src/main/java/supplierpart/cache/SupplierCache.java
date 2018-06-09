package supplierpart.cache;

import lombok.Data;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Data
@Component
public class SupplierCache {
    private static final Logger logger = LoggerFactory.getLogger(SupplierCache.class);


    private final String coBasePath = "http://10.131.245.91:8093";



    @Test
    public void getCurrentPath(){
        //<1>. The default is to get resources from the packages where this class is located;
//        System.out.println(this.getClass().getResource("").getPath());
        //<2>. get resources from the classpath root
//        System.out.println(this.getClass().getResource("/").getPath());
        //<3>. same as <2>
//        System.out.println(this.getClass().getClassLoader().getResource("").getPath());

        String path = SupplierCache.class.getResource("/").getPath();
        logger.info("path :"+path);
        Assert.notNull(path,"Not null");
    }



}
