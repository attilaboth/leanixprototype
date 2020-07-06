package com.telekom.timon.leanix.datamodel;

import org.junit.jupiter.api.AfterEach;
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
                "Sign | SIGN(P) (APPL149831)","T-VPP | T-VPP(P) (APPL149713)", "mShop | mShop(P) (APPL573735)", "CCOS | CCOS(P) (APPL150116)"));

        String foundDarwinName = testAppDarwinName.replaceApplicationNameWithDarwinName();

        Assertions.assertEquals("T-VPP(P) (APPL149713)", foundDarwinName);
    }


    @AfterEach
    void tearDown() {

    }
}