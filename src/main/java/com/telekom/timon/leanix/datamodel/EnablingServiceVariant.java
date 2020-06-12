package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnablingServiceVariant {

    private String enablingServiceVariantId;
    private String enablingServiceVariantName;
    private String esvUserLabel;
    private String esvDescription = "esvDescription"; //FIXME: tobe implemented
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

    /**
     * Login durchführen (mShop) | ESV-00200 | Login durchführen (mShop) | mShop
     * ESV-00200 - Login durchführen (mShop)
     * @return
     */
    public List<String> getESasXlsData() {
        List<String> esvAsXlsData = new ArrayList<>();
        String[] idAndName = enablingServiceVariantName.split("-");
        esvAsXlsData.add(idAndName[2]); //name
        esvAsXlsData.add(idAndName[0]+"-"+idAndName[1]); //implementation_id
        setEsvUserLabel(idAndName[2]); //FIXME: ? maybe this value is different
        esvAsXlsData.add(getEsvUserLabel()); //user_label
        esvAsXlsData.add(getEsvDescription());  //description

        return esvAsXlsData;

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EnablingServiceVariant that = (EnablingServiceVariant) o;
        return enablingServiceVariantId.equals(that.enablingServiceVariantId) &&
                enablingServiceVariantName.equals(that.enablingServiceVariantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enablingServiceVariantId, enablingServiceVariantName);
    }
}
