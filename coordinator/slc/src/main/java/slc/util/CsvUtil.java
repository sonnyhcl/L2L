package slc.util;

import com.csvreader.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slc.domain.LogisticsPart;
import slc.domain.SupplierPart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);


    public static List<SupplierPart> readSupplierParts(String filePath) throws IOException {
        List<SupplierPart> supplierParts = new ArrayList<SupplierPart>();
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
            SupplierPart supplierPart = new SupplierPart(orgName ,  orgId, host,  port, projectId, category , loc);
            supplierParts.add(supplierPart);
            logger.debug(supplierPart.toString());
        }
        return supplierParts;
    }

    public static List<LogisticsPart> readLogisticsParts(String filePath) throws IOException {
        List<LogisticsPart> logisticsParts = new ArrayList<LogisticsPart>();
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
            LogisticsPart logisticsPart = new LogisticsPart(orgName ,  orgId, host,  port, projectId, category);
            logisticsParts.add(logisticsPart);
            logger.debug(logisticsPart.toString());
        }
        return logisticsParts;
    }

}
