package vesselA.eventGateway;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShadowWithProcesses {
     private String vid;
     private List<String> pids = new ArrayList<String>();

     public void save(String pid){//temporaly support one shadow to be subscribed by one process.
         // pids.add(pid);
         if(pids.size() == 1){
             pids.set(0, pid);
         }else{
             pids.add(pid);
         }
     }

     public String findByPid(String pid){
         for(String s : pids){
             if(s.equals(pid)){
                 return vid;
             }
         }
         return null;
     }

     public  String getPid(){
         return pids.get(0);
     }
}
