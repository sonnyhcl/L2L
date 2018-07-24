package lvc.util;

import com.csvreader.CsvReader;
import lvc.domain.Freight;
import lvc.domain.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);
    public static List<Storage> readStorageRates(String filePath) throws IOException {
        List<Storage> storages = new ArrayList<Storage>();
        // 创建CSV读对象
        CsvReader csvReader = new CsvReader(filePath);
        if (csvReader.getHeaders() != null){
            logger.debug(csvReader.getHeaders().toString());
        }
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
        String line = null;
        while((line=reader.readLine())!=null){
            String[] items= line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
            String name = items[0].trim();
            Double rate = Double.parseDouble(items[1].trim());
            Storage storage = new Storage(name , rate);
            storages.add(storage);
            logger.debug(storage.toString());
        }
        return storages;
    }

}
