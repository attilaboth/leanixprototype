package com.telekom.timon.leanix.excel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class ExcelOperationsTest {

    private ExcelOperations excelOperations;
    private Map<String, List<String>> dummyDarwinNamesByFirstSheet;
    private Map<String, List<String>> dummyDarwinNamesByThirdSheet;

    @BeforeEach
    void setUp() {
        excelOperations = new ExcelOperations("/xlsFilesToBeParsed/dummy_DarwinNames_itcoNum_applicationNames.xlsx");
        dummyDarwinNamesByFirstSheet = new HashMap<>();
        dummyDarwinNamesByThirdSheet = new HashMap<>();

        dummyDarwinNamesByFirstSheet.put("ESV-ID", Collections.singletonList("Applikationsname DARWIN"));
        dummyDarwinNamesByFirstSheet.put("ESV-00136", Collections.singletonList("BL-T(P) (APPL116971)"));
        dummyDarwinNamesByFirstSheet.put("ESV-00002", Collections.singletonList("CRM-T_Prod_CAS (APPL470182)"));

        dummyDarwinNamesByThirdSheet.put("ESV-ID", Collections.singletonList("Applikationsname CAPE | " +
                "Applikationsname DARWIN"));
        dummyDarwinNamesByThirdSheet.put("ESV-00136", Collections.singletonList("BL-T | BL-T(P) (APPL116971)"));
        dummyDarwinNamesByThirdSheet.put("ESV-00002", Collections.singletonList("CRM-T | CRM-T_Prod_CAS (APPL470182)"));


    }

    @Test
    void generateSheetHeader() {
        Assertions.assertNotNull("generateSheetHeader");
    }

    @Test
    void generateDataRow() {
        Assertions.assertNotNull("generateDataRow");

    }

    @Test
    void generateDataFromObject() {
        Assertions.assertNotNull("generateDataFromObject");

    }

    @Test
    void getSpecificColumnsBySheetName() {
        Map<String, Set<String>> applicationRole = excelOperations.getSpecificColumnsBySheetName(
                "Application Role", 3, 2, false);

        Assertions.assertEquals(dummyDarwinNamesByFirstSheet, applicationRole);
        Assertions.assertNotNull("getSpecificColumnsBySheetName");

    }

    @Test
    void getSpecificColumnsBySheetNameDarwinName() {
        Map<String, Set<String>> applicationRole = excelOperations.getSpecificColumnsBySheetName(
                "Application Role", 3, 2, true);

        Assertions.assertEquals(dummyDarwinNamesByThirdSheet, applicationRole);
        Assertions.assertNotNull("getSpecificColumnsBySheetName");

    }
}