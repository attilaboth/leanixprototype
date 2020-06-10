package com.telekom.timon.leanix.datamodel;

public class AppDarwinName {

    private String appName;
    private String darwinName;
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
}
