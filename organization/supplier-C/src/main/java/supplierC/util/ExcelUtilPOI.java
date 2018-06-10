package supplierA.util;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtilPOI {
    private  final Logger logger = LoggerFactory.getLogger(ExcelUtilPOI.class);

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    public static Sheet getSheet(String fileName , String sheetName) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(fileName);
        if (fileName.endsWith(EXCEL_XLS)) {     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        } else if (fileName.endsWith(EXCEL_XLSX)) {    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        int numberOfSheets = wb.getNumberOfSheets();   //得到sheet的总数
        System.out.println("Sheet number of the file *.xls" + numberOfSheets);
        Sheet sheet = wb.getSheet(sheetName);
        in.close();

        return sheet;
    }


}
