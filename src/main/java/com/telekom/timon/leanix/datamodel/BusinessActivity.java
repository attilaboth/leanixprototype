package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;

public class BusinessActivity {

    private final String businessActivityId;
    private final String businessActivityName;
    private String businessActivityExternalId;
    private List<EnablingService> enablingServiceList;

    public BusinessActivity(final String businessActivityId, final String businessActivityName) {
        this.businessActivityId = businessActivityId;
        this.businessActivityName = businessActivityName;
    }

    public String getBusinessActivityExternalId() {
        return businessActivityExternalId.trim();
    }

    public BusinessActivity setBusinessActivityExternalId(final String businessActivityExternalId) {
        this.businessActivityExternalId = businessActivityExternalId;
        return this;
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
        return businessActivityName.trim();
    }

    /**
     * name	| id | ibi_teilprozess | network | teilbereich | teilprozess
     * @return
     */
    public List<String> getBaAsXlsData() {
        List<String> baIntoXlsAsData = new ArrayList<>();
        final String[] teilProcessAndName = businessActivityName.split(":");
        final String[] ntt = teilProcessAndName[1].split("-");

        //baIntoXlsAsData.add(teilProcessAndName[1] + " ("+businessActivityExternalId+")");//name
        baIntoXlsAsData.add(teilProcessAndName[1]);//nam
        baIntoXlsAsData.add(businessActivityExternalId); // BA-ID ?
        baIntoXlsAsData.add(teilProcessAndName[0]); // ibi_teilprozess

        //FIXME: How to handle not mathicng struture?
        // Auftragsmanagement - FN - Bereitstellung / Änderung / Kündigung / Umzug - Lösungsgeschäft (Bereitstellung/ Änderung/ Kündigung) (BA-0081)
        // Zentrale Infrastruktur - Single Sign On (E-SSO) - Anmeldung (BA-0160)
        if(ntt.length >= 3){
            baIntoXlsAsData.add(ntt[1]);//network
            baIntoXlsAsData.add(ntt[2]); //teilbereich
            baIntoXlsAsData.add(ntt[0]);//teilprozess
        }else{
            baIntoXlsAsData.add(ntt[1]);//network
            baIntoXlsAsData.add(ntt[0]);//teilprozess
        }

        return baIntoXlsAsData;

    }
}
