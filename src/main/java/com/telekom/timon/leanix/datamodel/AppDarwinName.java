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


    public String getAppName() {
        return appName;
    }

    public AppDarwinName setAppName(final String appName) {
        this.appName = appName;
        return this;
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

    //FIXME: make it more elegant, and use less String operations if possible
    public void findMyNameInPossibleNamesList() {
        String appNameNoPostFix = getAppName();
        System.out.println(appNameNoPostFix);

        this.darwinName = "<* " + getAppName().toUpperCase() + " *>"; //mShop --> MSHOP
        if(getAppName().contains(" ")){
            appNameNoPostFix = getAppName().split(" ")[0];
        }
        for (final String aDarwinName : darwinNameList) {
            //mShop(P) (APPL573735)
            if(aDarwinName.trim().isEmpty()){
                System.out.println("aDarwinName: " + aDarwinName + " .--> skippint iteration.");
                continue;
            }
            String aDarwinNameOnly = aDarwinName.trim().split(" ")[0]; //mShop(P) (APPL573735)
            String darwinWithoutP = (aDarwinNameOnly.contains("("))
                    ? aDarwinNameOnly.substring(0, aDarwinNameOnly.indexOf("("))
                    : aDarwinNameOnly;

            if(appNameNoPostFix.equalsIgnoreCase(darwinWithoutP)
                || darwinWithoutP.toUpperCase().contains(appNameNoPostFix.toUpperCase())){
                this.darwinName = aDarwinName;
                System.out.println(getAppName() + " == " + aDarwinName);
                return;
            }else{
                //System.out.println(getAppName() + " != " + aDarwinName);
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
