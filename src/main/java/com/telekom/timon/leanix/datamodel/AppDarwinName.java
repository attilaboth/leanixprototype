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
        return darwinName;
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

    //FIXME: make it more elegant, and use less String operations if possible
    //FIXME: delete if not used
    public void findMyNameInPossibleNamesList() {
        String appNameNoPostFix = getAppName();
        System.out.println(appNameNoPostFix);
        String appNameUpperCase = getAppName().toUpperCase();
        this.darwinName = "<* " + appNameUpperCase + " *>"; //mShop --> MSHOP

        if(getAppName().contains(" ")){
            appNameNoPostFix = getAppName().split(" ")[0];
        }

        for (final String aDarwinName : darwinNameList) {
            //mShop(P) (APPL573735)
            /*
            if(aDarwinName.trim().isEmpty()){
                System.out.println("aDarwinName: " + aDarwinName + " .--> skippint iteration.");
                continue;
            }
             */
            String aDarwinNameOnly = aDarwinName.trim().split(" ")[0]; //mShop(P) (APPL573735)
            String darwinWithoutP = (aDarwinNameOnly.contains("("))
                    ? aDarwinNameOnly.substring(0, aDarwinNameOnly.indexOf("("))
                    : aDarwinNameOnly;

            if(appNameNoPostFix.equalsIgnoreCase(darwinWithoutP)
                || darwinWithoutP.toUpperCase().contains(appNameNoPostFix.toUpperCase())){
                this.darwinName = aDarwinName;
                return;
            }else{
                //FIXME
                // use case : SOA BP -- becomes ---> iSMAR(P) (APPL149619)
                // there is no way to find the name matching in this case
                if(aDarwinName.equalsIgnoreCase("iSMAR(P) (APPL149619)")){
                    this.darwinName = aDarwinName;
                    System.out.println(appNameUpperCase + " -?- " + aDarwinName);
                }
            }
        }
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
