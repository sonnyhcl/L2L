package logisticpart.util;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class ExcelUtilJXL {
    private  final Logger logger = LoggerFactory.getLogger(ExcelUtilJXL.class);

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    public static HashMap<String , Double> readFreightRates(String fileName , String sheetName) throws IOException, BiffException {
        Sheet sheet = getSheet(fileName, sheetName);

        HashMap<String, Double> rates = new HashMap<String, Double>();
        DecimalFormat defaultFormat = new DecimalFormat("###.#####");
        for (int i = 1; i < sheet.getRows(); i++) {
            Cell c = sheet.getCell(0, i);
            String pname = c.getContents();
            NumberCell dc = (NumberCell) sheet.getCell(1, i);
            double rate = Double.parseDouble(defaultFormat.format(dc.getValue()));
            rates.put(pname, rate);
        }

        return rates;
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

    public static String getResourcePath() throws IOException, BiffException {
       String path =  ExcelUtilJXL.class.getResource("/").getPath();
       return path;
    }


}
