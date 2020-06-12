package com.telekom.timon.leanix.datamodel;

import java.util.ArrayList;
import java.util.List;

public class AppDarwinName implements Comparable<AppDarwinName>{

    private String appName;
    private String darwinName;
    private String itcoNumber = " (APPL_0000)";  //FIXME

    public AppDarwinName(final String appName) {
        this.appName = appName;
        this.darwinName = appName.toUpperCase();//FIXME
    }


    public String getAppName() {
        return appName;
    }

    public AppDarwinName setAppName(final String appName) {
        this.appName = appName;
        return this;
    }

    public String getDarwinName() {
        return darwinName;
    }

    public AppDarwinName setDarwinName(final String darwinName) {
        this.darwinName = darwinName;
        return this;
    }

    public String getItcoNumber() {
        return itcoNumber;
    }

    public AppDarwinName setItcoNumber(final String itcoNumber) {
        this.itcoNumber = itcoNumber;
        return this;
    }

    @Override
    public String toString() {
        return "appName=" + appName;
    }

    public List<String> getDarwinNameAsXslData() {
        List<String> darwinNameAsXls = new ArrayList<>();
        darwinNameAsXls.add(appName);
        darwinNameAsXls.add(darwinName + " " + itcoNumber);

        return darwinNameAsXls;
    }


    @Override
    public int compareTo(final AppDarwinName appDarwinName) {
        return this.getAppName().compareTo(appDarwinName.getAppName());
    }
}
