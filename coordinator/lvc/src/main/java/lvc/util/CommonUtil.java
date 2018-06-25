package lvc.util;

import lvc.domain.Bill;
import lvc.domain.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonUtil {
    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static Destination findByName(String name , List<Destination> destinations){
            for(Destination d : destinations){
                if(name.equals(d.getName())){
                    return  d;
                }
            }

            return  null;
    }

    public static double sumCost(String name , List<Bill> bills){
        for(Bill bill : bills){
            if(name.equals(bill.getDestination())){
                return bill.getFreightCost()+bill.getStorageCost();
            }
        }
        return 0.0;
    }

}
