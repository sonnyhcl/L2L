package vesseldevA.util;


import jxl.*;
import jxl.read.biff.BiffException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vesseldevA.domain.Destination;
import vesseldevA.domain.Location;
import vesseldevA.domain.VesselState;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SuppressWarnings("all")
public class ExcelUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";


    /**
     * 读取轮船轨迹数据 required all cells of string Type
     *
     * @param fileName
     * @param sheetName ： 以船号为sheet Name
     * @return
     * @throws IOException
     */
    public static List<VesselState> readTracjectory(String fileName, String sheetName) throws IOException, BiffException {
        Sheet sheet = getSheet(fileName, sheetName);

        List<VesselState> vesselStates = new ArrayList<VesselState>();
        for (int i = 0; i < sheet.getRows(); i++) {
            Cell c0 =  sheet.getCell(0, i);
            double logitude = Double.parseDouble(c0.getContents().toString());
            Cell c1 =  sheet.getCell(1, i);
            double latitude = Double.parseDouble(c1.getContents().toString());
            Cell c2=  sheet.getCell(2, i);
            String timeStamp = c2.getContents().trim();
            Cell c3 =  sheet.getCell(3, i);
            double velocity = Double.parseDouble(c3.getContents().toString());
            VesselState vesselState = new VesselState(logitude, latitude , velocity , timeStamp);
            vesselStates.add(vesselState);
        }
        return vesselStates;

    }

    public static List<Location> readPorts(String fileName , String sheetName) throws IOException, BiffException {
        Sheet sheet = getSheet(fileName, sheetName);

        List<Location> ports= new ArrayList<Location>();
        DecimalFormat defaultFormat = new DecimalFormat("###.######");
        for (int i = 1; i < sheet.getRows(); i++) {
            Cell c0= sheet.getCell(0, i);
            String name = c0.getContents().trim();
            NumberCell c1 = (NumberCell) sheet.getCell(1, i);
            double longitude = Double.parseDouble(defaultFormat.format(c1.getValue()));
            NumberCell c2 = (NumberCell) sheet.getCell(2, i);
            double latitude = Double.parseDouble(defaultFormat.format(c2.getValue()));
            Location port = new Location(name , longitude , latitude);
            ports.add(port);
        }

        return  ports;

    }

    public static List<Destination> readDestinations(String fileName , String sheetName) throws IOException, BiffException {
        Sheet sheet = getSheet(fileName, sheetName);
        List<Destination> destinations= new ArrayList<Destination>();
        DecimalFormat defaultFormat = new DecimalFormat("###.######");
        for (int i = 0; i < sheet.getRows(); i++) {
            Cell c0= sheet.getCell(0, i);
            String name = c0.getContents().trim();
            Cell c1 = sheet.getCell(1, i);
            String timeStamp = c1.getContents().trim();
            Destination destination = new Destination(name , timeStamp , timeStamp , null);
            destinations.add(destination);
        }
        return destinations;
    }



    public static Sheet getSheet(String fileName , String sheetName) throws IOException, BiffException {

        // 创建输入流, 读取box.xls
        InputStream box = new FileInputStream(fileName);
        // 获取box.xls文件对象
        Workbook boxExcel = Workbook.getWorkbook(box);
        // 获取box.xls文件的指定工作表, 默认的第一个
        Sheet sheet = boxExcel.getSheet(sheetName);
        return sheet;
    }


    @Test
    void test(){
        logger.debug(DateUtil.date2str(new Date()));
    }
}
