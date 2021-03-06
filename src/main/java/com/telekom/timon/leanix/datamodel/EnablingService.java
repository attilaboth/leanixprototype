package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnablingService implements Comparable<EnablingService>{

    private final String enablingServiceId;
    private final String enablingServiceName;
    private List<EnablingServiceVariant> enablingServiceVariantList;

    public EnablingService(final String enablingServiceId, final String enablingServiceName) {
        this.enablingServiceId = enablingServiceId;
        this.enablingServiceName = enablingServiceName;
    }


    public List<EnablingServiceVariant> getEnablingServiceVariantList() {
        if (null == enablingServiceVariantList) {
            enablingServiceVariantList = new ArrayList<>();
        }
        return enablingServiceVariantList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\n\tEnablingService{");
        sb.append("enablingServiceId='").append(enablingServiceId).append('\'');
        sb.append(", enablingServiceName='").append(enablingServiceName).append('\'');
        sb.append("\n\tenablingServiceVariantList=").append(enablingServiceVariantList);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EnablingService that = (EnablingService) o;
        return enablingServiceId.equals(that.enablingServiceId) &&
                enablingServiceName.equals(that.enablingServiceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enablingServiceId, enablingServiceName);
    }

    public String getEnablingServiceId() {
        return enablingServiceId;
    }

    public String getEnablingServiceName() {
        return enablingServiceName;
    }

    /**
     * ES-0006 - Kunden beraten & Angebot erstellen
     * name | es_id
     * @return
     */
    public List<String> getESasXlsData() {
        List<String> esAsXlsData = new ArrayList<>();
        String[] idAndName = enablingServiceName.split("-");
        esAsXlsData.add(idAndName[2]); // name
        esAsXlsData.add(idAndName[0]+"-"+idAndName[1]); // es_id

        return esAsXlsData;
    }

    @Override
    public int compareTo(final EnablingService enablingService) {
        return this.getEnablingServiceName().compareTo(enablingService.getEnablingServiceName());
    }
}
