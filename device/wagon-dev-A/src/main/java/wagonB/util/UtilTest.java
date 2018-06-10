package wagonB.util;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wagonB.domain.Port;
import wagonB.domain.VesselState;

import java.io.IOException;
import java.util.List;

public class UtilTest {
    private  static Logger logger = LoggerFactory.getLogger(UtilTest.class);


    private static List<Port> ports;

    public static void main(String[] args) throws IOException {
        String fileName = "vdata.xls";
        String sheetName  = "413362260";
       // String rootPath= new UtilTest().getClass().getResource("/").getFile().toString();
        String rootPath = UtilTest.getCurrentPath();
        System.out.println(rootPath);
        long start =  System.currentTimeMillis();
        List<VesselState> vesselStates = ExcelUtil.readVesselStates(rootPath+fileName , sheetName);
        for(VesselState e : vesselStates){
            System.out.println(e.toString());
        }
        long end =  System.currentTimeMillis();

        System.out.println("耗时 ： "+(end - start) +" ms");
    }

    public static String getCurrentPath(){
        //取得根目录路径
        String rootPath= new UtilTest().getClass().getResource("/").getFile().toString();
//        //当前目录路径
//        String currentPath1=getClass().getResource(".").getFile().toString();
//        String currentPath2=getClass().getResource("").getFile().toString();
//        //当前目录的上级目录路径
//        String parentPath=getClass().getResource("../").getFile().toString();

        return rootPath;

    }
    @Test
    public void test(){
        String path = UtilTest.class.getResource("/").getPath();
        try {
            logger.info("path : "+path);
            ports = ExcelUtil.readPorts(path+"manager_ports.xls" , "info");
            logger.info("ports : "+ports);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
