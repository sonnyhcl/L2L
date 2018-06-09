package managerpart.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Data
@Component
public class CommonRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String VMCRootPath = "http://10.131.245.91:8080/vmc";
}
