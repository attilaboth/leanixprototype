package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;

public class AppDarwinName implements Comparable<AppDarwinName>{

    private String appName;
    private String darwinName;
    private List<String> darwinNameList;
    private String itcoNumber;

    public AppDarwinName(final String appName) {
        this.appName = appName;
    }

    public AppDarwinName setDarwinName(final String darwinName) {
        this.darwinName = darwinName;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public List<String> getDarwinNameList() {
        if(null == darwinNameList){
            darwinNameList = new ArrayList<>();
        }
        return darwinNameList;
    }

    public String getDarwinName() {
        //IT Universe stores all in uppercase
        return darwinName.toUpperCase();
    }

    public List<String> getDarwinNameAsXslData() {
        List<String> darwinNameAsXls = new ArrayList<>();

        darwinNameAsXls.add(getDarwinName());

        return darwinNameAsXls;
    }

    public String replaceApplicationNameWithDarwinName() {
        // (1) direct match search
        for (final String aDarwinName : darwinNameList) {
            String anApplName = aDarwinName.substring(0, aDarwinName.indexOf(" | "));
            String darwinName = aDarwinName.substring(aDarwinName.indexOf(" | ")+3, aDarwinName.length());
            if(getAppName().equalsIgnoreCase(anApplName)){
                return darwinName;
            }
        }
        // (2) indirect match search
        String applicationName = getAppName();
        if(applicationName.contains(" ")){
            applicationName = getAppName().split(" ")[0];
        }
        for (final String aDarwinName : darwinNameList) {
            String anApplName = aDarwinName.substring(0, aDarwinName.indexOf(" | "));
            String darwinName = aDarwinName.substring(aDarwinName.indexOf(" | ")+3, aDarwinName.length());
            if(applicationName.equalsIgnoreCase(anApplName)){
                return darwinName;

            }
        }
        return "<* " + getAppName() + " *>";
    }

    @Override
    public int compareTo(final AppDarwinName appDarwinName) {
        return this.getAppName().compareTo(appDarwinName.getAppName());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppDarwinName{");
        sb.append("appName='").append(appName).append('\'');
        sb.append(", darwinName='").append(darwinName).append('\'');
        sb.append(", darwinNameList=").append(getDarwinNameList());
        sb.append('}');
        return sb.toString();
    }


}
