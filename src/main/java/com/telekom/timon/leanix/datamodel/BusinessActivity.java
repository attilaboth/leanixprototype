package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;

public class BusinessActivity {

    private final String businessActivityId;
    private final String businessActivityName;
    private List<EnablingService> enablingServiceList;

    public BusinessActivity(final String businessActivityId, final String businessActivityName) {
        this.businessActivityId = businessActivityId;
        this.businessActivityName = businessActivityName;
    }

    public List<EnablingService>  getEnablingServiceList() {
        if(null == enablingServiceList){
            enablingServiceList = new ArrayList<>();
        }
        return enablingServiceList;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BusinessActivity{");
        sb.append("businessActivityId='").append(businessActivityId).append('\'');
        sb.append(", businessActivityName='").append(businessActivityName).append('\'');
        sb.append("\nenablingServiceList=").append(enablingServiceList);
        sb.append('}');
        return sb.toString();
    }

    public String getBusinessActivityId() {
        return businessActivityId;
    }

    public String getBusinessActivityName() {
        return businessActivityName;
    }
}
