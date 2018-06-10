package msc.domain;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelUtilJXL {
    private  final Logger logger = LoggerFactory.getLogger(ExcelUtilJXL.class);

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

//    private static List<Port> ports;
//    public static List<Port> readPorts(String fileName , String sheetName) throws IOException, BiffException {
//        Sheet sheet = getSheet(fileName,sheetName);
//
//        List<Port> ports = new ArrayList<Port>();
//
//        for(int i = 1 ; i < sheet.getRows();i++){
//            Cell c = sheet.getCell(0 , i);
//            String pname = c.getContents();
//            c = sheet.getCell(1 ,i);
//            Double storageRate = Double.parseDouble(c.getContents());
//            c = sheet.getCell(2 ,i);
//            Boolean craneStart = Boolean.parseBoolean(c.getContents());
//            c = sheet.getCell(3,i);
//            Double weightLimit = Double.parseDouble(c.getContents());
//            Port port = new Port(pname, storageRate, craneStart, weightLimit);
//
//            ports.add(port);
//        }
//        if (ports.size() > 0) {
//            return ports;
//        } else {
//            return null;
//        }
//    }


    public static Sheet getSheet(String fileName , String sheetName) throws IOException, BiffException {

        // 创建输入流, 读取box.xls
        InputStream box = new FileInputStream(fileName);
        // 获取box.xls文件对象
        Workbook boxExcel = Workbook.getWorkbook(box);
        // 获取box.xls文件的指定工作表, 默认的第一个
        Sheet sheet = boxExcel.getSheet(sheetName);
        return sheet;
    }

//    @Test
//    public void test() throws IOException, BiffException {
//        String path = ExcelUtilJXL.class.getResource("/").getPath();
//
//        logger.info("path : "+path);
//        ports = ExcelUtilJXL.readPorts(path+"manager_ports.xls" , "info");
//        logger.info("ports : "+ports);
//
//    }
}
