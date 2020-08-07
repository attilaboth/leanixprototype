package com.telekom.timon.leanix.datamodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class AppDarwinNameTest {

    private AppDarwinName testAppDarwinName;

    @BeforeEach
    void setUp() {
        testAppDarwinName = new AppDarwinName("CCOS");
    }

    @Test
    void replaceApplicationNameWithDarwinNameTest() {

        testAppDarwinName.getDarwinNameList().addAll(Arrays.asList(
                "Sign | SIGN(P) (APPL149831)","T-VPP | T-VPP(P) (APPL149713)", "mShop | mShop(P) (APPL573735)", "CCOS | CCOS(P) (APPL150116)"));

        String foundDarwinName = testAppDarwinName.replaceApplicationNameWithDarwinName();

        Assertions.assertEquals("CCOS(P) (APPL150116)", foundDarwinName);
    }

    @Test
    void replaceApplicationNameWithDarwinNameTest2() {

        testAppDarwinName.getDarwinNameList().addAll(Arrays.asList(
                "Sign | SIGN(P) (APPL149831)","T-VPP | T-VPP(P) (APPL149713)", "mShop | mShop(P) (APPL573735)"));

        String foundDarwinName = testAppDarwinName.replaceApplicationNameWithDarwinName();

        Assertions.assertEquals("<* " + testAppDarwinName.getAppName() + " *>", foundDarwinName);
    }

    @Test
    void replaceApplicationNameWithDarwinNameTest3() {
        AppDarwinName testAppDarwinName = new AppDarwinName("T-VPP Core");
        testAppDarwinName.getDarwinNameList().addAll(Arrays.asList(
                "Sign | SIGN(P) (APPL149831)", "T-VPP | T-VPP(P) (APPL149713)", "mShop | mShop(P) (APPL573735)", "CCOS | CCOS(P) (APPL150116)"));

        String foundDarwinName = testAppDarwinName.replaceApplicationNameWithDarwinName();

        Assertions.assertEquals("T-VPP(P) (APPL149713)", foundDarwinName);
    }


    @Test
    void checkForPartialMatch() {
        final boolean fullMatchFound = AppDarwinName.checkForPartialMatch("MSHOP (APPL12345)", "MSHOP (APPL12345)");
        Assertions.assertTrue(fullMatchFound);

        final boolean partialMatchFound = AppDarwinName.checkForPartialMatch("MSHOP (APPL12345)", "MSHOP (APPL23451)");
        Assertions.assertFalse(partialMatchFound);

        final boolean noMatchFound = AppDarwinName.checkForPartialMatch("MSHOP (APPL12345)", "MSHOP CITY (APPL23451)");
        Assertions.assertFalse(noMatchFound);

    }
}