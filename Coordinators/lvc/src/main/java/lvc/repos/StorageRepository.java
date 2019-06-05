package lvc.repos;

import lombok.Data;
import lvc.util.CsvUtil;
import lvc.domain.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationHome;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Data
@Service
public class StorageRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Storage> storages;

    public StorageRepository(@Value("${storages.fileName}") String fileName) throws IOException {
        //TODO:Temporarily read storage rates from storageRates.xls which should be initiated by port register.
        logger.info("--"+fileName+"--");
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        System.out.println(jarF.getParentFile().toString());
        String filePath = jarF.getParentFile().toString()+"/classes/"+fileName;
        storages = CsvUtil.readStorageRates(filePath);
    }

    public double findByName(String name){
        for(Storage storage : storages){
            if(name.equals(storage.getName())){
                return storage.getStorageRate();
            }
        }
        return 0.0;
    }

}
