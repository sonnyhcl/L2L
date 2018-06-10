package wagonB.util;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wagonB.domain.Port;
import wagonB.domain.VesselState;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";


    /**
     * 读取轮船轨迹数据
     *
     * @param fileName
     * @param sheetName ： 以船号为sheet Name
     * @return
     * @throws IOException
     */
    public static List<VesselState> readVesselStates(String fileName, String sheetName) throws IOException {

        Sheet sheet = getSheet(fileName,sheetName);

        List<VesselState> vesselStates = new ArrayList<VesselState>();


        for (Row r : sheet) {

            r.getCell(0).setCellType(CellType.STRING);
            String vId = r.getCell(0).getStringCellValue();
            Double longitude = Double.parseDouble(r.getCell(1).getStringCellValue());
            Double latitude = Double.parseDouble(r.getCell(2).getStringCellValue());
            String date = r.getCell(3).getStringCellValue();
            Double velocity = Double.parseDouble(r.getCell(4).getStringCellValue());

            VesselState vesselState = new VesselState(longitude, latitude, velocity, date);

            vesselStates.add(vesselState);
        }

        if (vesselStates.size() > 0) {
            return vesselStates;
        } else {
            return null;
        }

    }

    public static List<Port> readPorts(String fileName , String sheetName) throws IOException {
        Sheet sheet = getSheet(fileName,sheetName);

        List<Port> ports = new ArrayList<Port>();


        for (Row r : sheet) {

            r.getCell(0).setCellType(CellType.STRING);
            String vId = r.getCell(0).getStringCellValue();
            r.getCell(1).setCellType(CellType.STRING);
            Double longitude = Double.parseDouble(r.getCell(1).getStringCellValue());
            r.getCell(2).setCellType(CellType.STRING);
            Double latitude = Double.parseDouble(r.getCell(2).getStringCellValue());
            String date1 = DateUtil.date2str(r.getCell(3).getDateCellValue());
            String date2 = date1;

            Port port = new Port(vId, longitude, latitude, date1, date2);

            ports.add(port);
        }

        if (ports.size() > 0) {
            return ports;
        } else {
            return null;
        }
    }


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
