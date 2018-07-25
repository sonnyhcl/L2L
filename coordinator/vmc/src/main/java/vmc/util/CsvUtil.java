package vmc.util;

import com.csvreader.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vmc.domain.ManagerPart;
import vmc.domain.VesselPart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);
    public static List<VesselPart> readVesselParts(String filePath) throws IOException {
        List<VesselPart> vesselParts = new ArrayList<VesselPart>();
        // 创建CSV读对象
        CsvReader csvReader = new CsvReader(filePath);
        if (csvReader.getHeaders() != null){
            logger.debug(csvReader.getHeaders().toString());
        }
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        while((line=reader.readLine())!=null){
            String[] items= line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
            String orgName = items[0].trim();
            String orgId = items[1].trim();
            String host = items[2].trim();
            String port = items[3].trim();
            String projectId = items[4].trim();
            String category = items[5].trim();
            VesselPart vesselPart = new VesselPart(orgName ,  orgId, host,  port, projectId, category);
            vesselParts.add(vesselPart);
            logger.debug(vesselPart.toString());
        }
        return vesselParts;
    }

    public static List<ManagerPart> readManagerParts(String filePath) throws IOException {
        List<ManagerPart> managerParts = new ArrayList<ManagerPart>();
        // 创建CSV读对象
        CsvReader csvReader = new CsvReader(filePath);
        if (csvReader.getHeaders() != null){
            logger.debug(csvReader.getHeaders().toString());
        }
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        while((line=reader.readLine())!=null){
            String[] items= line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
            String orgName = items[0].trim();
            String orgId = items[1].trim();
            String host = items[2].trim();
            String port = items[3].trim();
            String projectId = items[4].trim();
            String category = items[5].trim();
            String loc = items[6].trim();
            ManagerPart managerPart = new ManagerPart(orgName ,  orgId, host,  port, projectId, category , loc);
            managerParts.add(managerPart);
            logger.debug(managerPart.toString());
        }
        return managerParts;
    }

}
