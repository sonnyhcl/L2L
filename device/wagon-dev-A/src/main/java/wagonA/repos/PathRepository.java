package wagonA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wagonA.domain.Path;


@Data
@Service
@SuppressWarnings("all")
public class PathRepository {
    private static final Logger logger = LoggerFactory.getLogger(PathRepository.class);
    private Path path;

    public void save(Path path){
        this.path = path;
    }

}
