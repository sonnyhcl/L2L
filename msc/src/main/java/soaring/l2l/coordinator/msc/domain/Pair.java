package soaring.l2l.coordinator.msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    private ManagerPart managerPart;
    private SupplierPart supplierPart;

    public boolean isRegistried(String orgId , String pid){
        if(this.managerPart != null){
            String vOrgId = this.managerPart .getOrgId();
            String vPid = this.managerPart .getPid();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }
        }

        if(this.supplierPart != null){
            String mOrgId = this.supplierPart.getOrgId();
            String mPid = this.supplierPart.getPid();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        if(this.managerPart != null && this.supplierPart != null){
            String vOrgId = this.managerPart.getOrgId();
            String vPid = this.managerPart.getPid();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }

            String mOrgId = this.supplierPart.getOrgId();
            String mPid = this.supplierPart.getPid();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return isPaired;
    }
}
