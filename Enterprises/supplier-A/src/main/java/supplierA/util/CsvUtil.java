package supplierA.util;

import com.csvreader.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import supplierA.domain.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    public static List<Location> readLocations(String filePath) throws IOException {
        List<Location> locations= new ArrayList<Location>();
        // 创建CSV读对象
        CsvReader csvReader = new CsvReader(filePath);
        if (csvReader.getHeaders() != null){
            logger.debug(csvReader.getHeaders().toString());
        }
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        while((line=reader.readLine())!=null){
            String[] items= line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
            String name = items[0].trim();
            Double lng = Double.parseDouble(items[1].trim());
            Double lat = Double.parseDouble(items[2].trim());
            Location location = new Location(name , lng , lat);
            locations.add(location);
        }
        return  locations;
    }

}
