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

    /**
     * name	| id | ibi_teilprozess | network | teilbereich | teilprozess
     * @return
     */
    public List<String> getBaAsXlsData() {
        List<String> baIntoXlsAsData = new ArrayList<>();
        final String[] teilProcessAndName = businessActivityName.split(":");
        final String[] ntt = teilProcessAndName[1].split("-");

        //FIXME: Where do we find this?
        final String BA_ID = "<BA_ID000>";

        baIntoXlsAsData.add(teilProcessAndName[1] + " ("+BA_ID+")");//name
        baIntoXlsAsData.add(BA_ID); // BA-ID ?
        baIntoXlsAsData.add(teilProcessAndName[0]); // ibi_teilprozess
        baIntoXlsAsData.add(ntt[1]);//network
        baIntoXlsAsData.add(ntt[2]); //teilbereich
        baIntoXlsAsData.add(ntt[0]);//teilprozess

        return baIntoXlsAsData;

    }
}
