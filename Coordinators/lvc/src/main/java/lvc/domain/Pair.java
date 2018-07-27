package lvc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class Pair {
    private VesselProcessInstance vpi;
    private LogisticsProcessInstance lpi;


    public boolean isRegistried(String orgId , String pid){
        if(this.lpi != null){
            String vOrgId = this.lpi .getOrgId();
            String vPid = this.lpi .getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }
        }

        if(this.vpi != null){
            String mOrgId = this.vpi.getOrgId();
            String mPid = this.vpi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        if(this.lpi != null && this.vpi != null){
            String vOrgId = this.lpi.getOrgId();
            String vPid = this.lpi.getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }

            String mOrgId = this.vpi.getOrgId();
            String mPid = this.vpi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return isPaired;
    }
}
