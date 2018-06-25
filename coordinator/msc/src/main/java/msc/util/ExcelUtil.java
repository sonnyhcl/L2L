package msc.util;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import msc.domain.Crane;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
public class ExcelUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    /**
     * read crane
     * @param fileName
     * @param sheetName
     * @return
     * @throws IOException
     * @throws BiffException
     */
    public static List<Crane> readCranes(String fileName , String sheetName) throws IOException, BiffException {
        Sheet sheet = getSheet(fileName, sheetName);
        List<Crane> destinations= new ArrayList<Crane>();
        DecimalFormat defaultFormat = new DecimalFormat("###.######");
        for (int i = 0; i < sheet.getRows(); i++) {
            Cell c0= sheet.getCell(0, i);
            String name = c0.getContents().trim();
            Cell c1 =  sheet.getCell(1, i);
            double weightLimit = Double.parseDouble(c1.getContents().toString());
            Crane destination = new Crane(name ,true , weightLimit);
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
}
