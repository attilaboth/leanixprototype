package com.telekom.timon.leanix.datamodel;

import com.telekom.timon.leanix.ucmdbdata.Cis;
import com.telekom.timon.leanix.ucmdbdata.UcmdbDataContainer;
import com.telekom.timon.leanix.util.Spelling;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AppDarwinName implements Comparable<AppDarwinName> {

    private static final Logger logger = LogManager.getLogger(AppDarwinName.class);


    private final String appName;
    private String darwinName;
    private Set<String> darwinNameList;
    private static List<String> mismatchingApplicationNames;
    private static List<String> mismatchingDarwinNames;
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

    public Set<String> getDarwinNameList() {
        if (null == darwinNameList) {
            darwinNameList = new TreeSet<>();
        }
        return darwinNameList;
    }

    public static List<String> getMismatchingApplicationNames() {
        if (mismatchingApplicationNames == null) {
            mismatchingApplicationNames = new ArrayList<>();
        }
        return mismatchingApplicationNames;
    }

    public static List<String> getMismatchingDarwinNames() {
        if (mismatchingDarwinNames == null) {
            mismatchingDarwinNames = new ArrayList<>();
        }
        return mismatchingDarwinNames;
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
            String darwinName = aDarwinName.substring(aDarwinName.indexOf(" | ")+3);
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
            String darwinName = aDarwinName.substring(aDarwinName.indexOf(" | ") + 3);
            if (applicationName.equalsIgnoreCase(anApplName)) {
                return darwinName;
            }
        }

        logger.info("Could not replace Application name withDarwin Name (XLS) from: " + getAppName());

        if (!getMismatchingApplicationNames().contains(getAppName())) {
            getMismatchingApplicationNames().add(getAppName());
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

    public static boolean isDarwinFoundInUcmdb(final AppDarwinName appDarwinName, final UcmdbDataContainer ucmdbData) {

        boolean isPartialMatchFound = false;

        for (Cis ucmdbCis : ucmdbData.getCis()) {
            String ucmdbName = ucmdbCis.getProperties().getName();

            if (appDarwinName.getDarwinName().equalsIgnoreCase(ucmdbName)) {
                //System.out.println(appDarwinName.getDarwinName() +" >>> "+ ucmdbName);
                return true;
            } else {
                //return isDarwinFoundInUcmdbCheckedWithTypos(appDarwinName.getAppName(), ucmdbName);
                //FIXME: check partial match in UCMDB
                isPartialMatchFound = checkForPartialMatch(appDarwinName.getDarwinName(), ucmdbName);

                if (isPartialMatchFound) {
                    String result = appDarwinName.getDarwinName() + " >>> " + ucmdbName;

                    if (!getMismatchingDarwinNames().contains(result)) {
                        getMismatchingDarwinNames().add(result);
                    }

                    System.out.println("Partial match is not found for: " + appDarwinName.getDarwinName() + " >>> " + ucmdbName);
                }
            }
        }

        System.out.println(appDarwinName.getAppName() + " is not found in uCMDB");
        return isPartialMatchFound;
    }

    //MSHOP (APPL12345)  -  MSHOP (APPL23451)
    public static boolean checkForPartialMatch(final String appName, final String ucmdbName) {
        if (appName.length() == ucmdbName.length()) {
            for (int i = 0; i < appName.length(); i++) {
                if (appName.charAt(i) != ucmdbName.charAt(i)) {
                    //!! LOG both appName and ucmdbName
                    //System.out.println("2."+appName + " != "+ ucmdbName);
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private static boolean isDarwinFoundInUcmdbCheckedWithTypos(final String appName, final String ucmdbName) {
        Spelling.checkSpelling(appName);

        String correctDarwinName = Spelling.correct(appName);
        System.out.println("Corrected Darwin name: " + correctDarwinName);

        if (!correctDarwinName.equals(ucmdbName)) {
            System.out.println("Darwin name: " + "'" + appName + "'" +
                    " and uCMDB name: " + "'" + ucmdbName + "'" + " are not matching!");
            //TODO: create a log here.
            return false;
        } else {
            //Lucene project: This method works fast, but is very complex to implement
            return true;
        }
    }
}
