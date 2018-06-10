package vmc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class Pair {
    private VesselProcessInstance vpi;//manager process instance
    private ManagerProcessInstance mpi;//supplier process instance

    public boolean isRegistried(String orgId , String pid){
        if(this.vpi != null){
            String vOrgId = this.vpi .getOrgId();
            String vPid = this.vpi .getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }
        }

        if(this.mpi != null){
            String mOrgId = this.mpi.getOrgId();
            String mPid = this.mpi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        if(this.vpi != null && this.mpi != null){
            String vOrgId = this.vpi.getOrgId();
            String vPid = this.vpi.getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }

            String mOrgId = this.mpi.getOrgId();
            String mPid = this.mpi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return isPaired;
    }
}
