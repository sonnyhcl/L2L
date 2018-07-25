package msc.util;

import com.csvreader.CsvReader;
import msc.domain.Crane;
import msc.domain.ManagerPart;
import msc.domain.SupplierPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);
    public static List<Crane> readCranes(String filePath) throws IOException {
        List<Crane> cranes = new ArrayList<Crane>();
        // 创建CSV读对象
        CsvReader csvReader = new CsvReader(filePath);
        if (csvReader.getHeaders() != null){
            logger.debug(csvReader.getHeaders().toString());
        }
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
//        reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
        String line = null;
        while((line=reader.readLine())!=null){
            String[] items= line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
            String name = items[0].trim();
            Double weightLimit = Double.parseDouble(items[1].trim());
            Crane crane = new Crane(name , true , weightLimit);
            cranes.add(crane);
        }
        return cranes;
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

}
