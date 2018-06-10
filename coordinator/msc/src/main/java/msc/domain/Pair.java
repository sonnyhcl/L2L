package msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class Pair {
    private ProcessInstance mpi;//manager process instance
    private ProcessInstance spi;//supplier process instance

    public boolean isRegistried(String orgId , String pid){
        if(this.mpi != null){
            String vOrgId = this.mpi .getOrgId();
            String vPid = this.mpi .getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }
        }

        if(this.spi != null){
            String mOrgId = this.spi.getOrgId();
            String mPid = this.spi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return false;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        if(this.mpi != null && this.spi != null){
            String vOrgId = this.mpi.getOrgId();
            String vPid = this.mpi.getId();
            if(orgId.equals(vOrgId) && pid.equals(vPid)){
                return true;
            }

            String mOrgId = this.spi.getOrgId();
            String mPid = this.spi.getId();
            if(orgId.equals(mOrgId) && pid.equals(mPid)){
                return true;
            }
        }

        return isPaired;
    }
}
