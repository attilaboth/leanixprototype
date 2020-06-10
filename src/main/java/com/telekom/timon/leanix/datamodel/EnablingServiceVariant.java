package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;

public class EnablingServiceVariant {

    private String enablingServiceVariantId;
    private String enablingServiceVariantName;
    private String esvUserLabel;
    private String esvDescription;
    private List<AppDarwinName> appDarwinNameList;

    public EnablingServiceVariant(final String enablingServiceVariantId, final String enablingServiceVariantName) {
        this.enablingServiceVariantId = enablingServiceVariantId;
        this.enablingServiceVariantName = enablingServiceVariantName;
    }


    public String getEnablingServiceVariantName() {
        return enablingServiceVariantName;
    }

    public EnablingServiceVariant setEnablingServiceVariantName(final String enablingServiceVariantName) {
        this.enablingServiceVariantName = enablingServiceVariantName;
        return this;
    }

    public String getEnablingServiceVariantId() {
        return enablingServiceVariantId;
    }

    public EnablingServiceVariant setEnablingServiceVariantId(final String enablingServiceVariantId) {
        this.enablingServiceVariantId = enablingServiceVariantId;
        return this;
    }

    public String getEsvUserLabel() {
        return esvUserLabel;
    }

    public EnablingServiceVariant setEsvUserLabel(final String esvUserLabel) {
        this.esvUserLabel = esvUserLabel;
        return this;
    }

    public String getEsvDescription() {
        return esvDescription;
    }

    public EnablingServiceVariant setEsvDescription(final String esvDescription) {
        this.esvDescription = esvDescription;
        return this;
    }

    public List<AppDarwinName> getAppDarwinNameList() {
        if (null == appDarwinNameList) {
            appDarwinNameList = new ArrayList<>();
        }
        return appDarwinNameList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\n\t\tEnablingServiceVariant{");
        sb.append("enablingServiceVariantId='").append(enablingServiceVariantId).append('\'');
        sb.append(",enablingServiceVariantName='").append(enablingServiceVariantName).append('\'');
        sb.append("\n\t\tappDarwinNameList=").append(appDarwinNameList);
        sb.append('}');
        return sb.toString();
    }
}
