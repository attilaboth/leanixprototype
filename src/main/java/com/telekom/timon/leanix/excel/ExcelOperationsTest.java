package com.telekom.timon.leanix.excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Arrays;

public class ExcelOperationsTest {

    public static void main(String[] args) {

        ExcelOperations excelWritter = new ExcelOperations("xlsFiles/BCC.xls");


        XSSFSheet relationshipsSheet = excelWritter.createASheet("relationships");
        excelWritter.generateSheetHeader(relationshipsSheet, Arrays.asList("start", "relation_type", "end"));
        excelWritter.generateDataRow(relationshipsSheet,Arrays.asList("Attila", "married", "Both"),1);

        XSSFSheet esvSheet = excelWritter.createASheet("enabling_service_variant");
        excelWritter.generateSheetHeader(esvSheet, Arrays.asList("start", "implementation_id", "user_label", "description"));



        excelWritter.generateFinalXslFile();
    }

}
