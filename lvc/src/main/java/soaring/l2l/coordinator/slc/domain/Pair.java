package soaring.l2l.coordinator.slc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    private SupplierPart supplierPart;
    private LogisticPart logisticPart;

    public boolean isRegistried(String orgId , String pid){
        if(this.supplierPart != null){
            String vOrgId = this.supplierPart .getOrgId();
            String vPid = this.supplierPart .getPid();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }
        }

        if(this.logisticPart != null){
            String mOrgId = this.logisticPart.getOrgId();
            String mPid = this.logisticPart.getPid();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        if(this.supplierPart != null && this.logisticPart != null){
            String vOrgId = this.supplierPart.getOrgId();
            String vPid = this.supplierPart.getPid();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }

            String mOrgId = this.logisticPart.getOrgId();
            String mPid = this.logisticPart.getPid();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return isPaired;
    }
}
