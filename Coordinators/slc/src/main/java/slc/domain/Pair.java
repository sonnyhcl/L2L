package slc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class Pair {
    private SupplierProcessInstance spi;
    private LogisticProcessInstance lpi;

    public boolean isRegistried(String orgId , String pid){
        if(this.spi != null){
            String vOrgId = this.spi .getOrgId();
            String vPid = this.spi .getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }
        }

        if(this.lpi != null){
            String mOrgId = this.lpi.getOrgId();
            String mPid = this.lpi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        if(this.spi != null && this.lpi != null){
            String vOrgId = this.spi.getOrgId();
            String vPid = this.spi.getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }

            String mOrgId = this.lpi.getOrgId();
            String mPid = this.lpi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return isPaired;
    }
}
