package soaring.l2l.coordinator.vmc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    private VesselPart vesselPart;
    private ManagerPart managerPart;

    public boolean isRegistried(String orgId, String pid) {
        if (this.vesselPart != null) {
            String vOrgId = this.vesselPart.getOrgId();
            String vPid = this.vesselPart.getPid();
            if (orgId.equals(vOrgId) && pid.equals(vPid)) {
                return true;
            }
        }

        if (this.managerPart != null) {
            String mOrgId = this.managerPart.getOrgId();
            String mPid = this.managerPart.getPid();
            if (orgId.equals(mOrgId) && pid.equals(mPid)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId, String pid) {
        boolean isPaired = false;

        if (this.vesselPart != null && this.managerPart != null) {
            String vOrgId = this.vesselPart.getOrgId();
            String vPid = this.vesselPart.getPid();
            if (orgId.equals(vOrgId) && pid.equals(vPid)) {
                return true;
            }

            String mOrgId = this.managerPart.getOrgId();
            String mPid = this.managerPart.getPid();
            if (orgId.equals(mOrgId) && pid.equals(mPid)) {
                return true;
            }
        }

        return isPaired;
    }
}
