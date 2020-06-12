package com.telekom.timon.leanix.excel;

import com.telekom.timon.leanix.datamodel.BusinessActivity;
import com.telekom.timon.leanix.datamodel.EnablingService;
import com.telekom.timon.leanix.datamodel.EnablingServiceVariant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcelOperations {

    private XSSFWorkbook workbook;
    private String xlsFileName;

    public ExcelOperations(final String xlsFileName) {
        this.xlsFileName = xlsFileName;
        this.workbook = new XSSFWorkbook();
    }

    public String getXlsFileName() {
        return xlsFileName;
    }

    public XSSFSheet createASheet(final String sheetName) {
        return workbook.createSheet(sheetName);
    }

    public Row createARow(final XSSFSheet aSheet, final int rowNum) {
        return aSheet.createRow(rowNum);
    }

    public Cell creteACell(final Row aRow, final int cellNum) {
        return aRow.createCell(cellNum);
    }

    public void generateSheetHeader(final XSSFSheet xssfSheet, final List<String> headerTextList) {
        Row aRow = this.createARow(xssfSheet, 0);
        int cellNum = 0;
        for (String aHeaderTxt : headerTextList) {
            creteACell(aRow, cellNum++).setCellValue(aHeaderTxt);
        }
    }

    public void generateDataRow(final XSSFSheet xssfSheet, final List<String> dataAsList, int rowNum) {
        int rowNumber = rowNum;
        Row aRow = this.createARow(xssfSheet, rowNumber++);
        int cellNum = 0;
        for (String aDataValue : dataAsList) {
            creteACell(aRow, cellNum++).setCellValue(aDataValue);
        }
    }

    public void generateDataFromObject(final List<BusinessActivity> businessActivityList) {
        XSSFSheet business_activitySheet = createASheet("business_activity");
        generateSheetHeader(business_activitySheet, Arrays.asList("name", "id", "ibi_teilprozess","network","teilbereich","teilprozess"));

        XSSFSheet enabling_serviceSheet = createASheet("enabling_service");
        generateSheetHeader(enabling_serviceSheet, Arrays.asList("name", "es_id"));
        Set<EnablingService> enablingServiceTabSet = new HashSet<>();

        XSSFSheet enabling_service_variantSheet = createASheet("enabling_service_variant");
        generateSheetHeader(enabling_service_variantSheet, Arrays.asList("name", "implementation_id", "user_label", "description"));
        Set<EnablingServiceVariant> enablingServiceVariantTabSet = new HashSet<>();

        int baRowNum = 0;
        for (BusinessActivity aBusinessActivity: businessActivityList) {

            //business_activity tab
            generateDataRow(business_activitySheet,aBusinessActivity.getBaAsXlsData(), ++baRowNum);

            //enabling_service tab
            for (EnablingService anEnablingService: aBusinessActivity.getEnablingServiceList()) {
                enablingServiceTabSet.add(anEnablingService);

                //enabling_service_variant tab
                final List<EnablingServiceVariant> enablingServiceVariantList = anEnablingService.getEnablingServiceVariantList();
                for (EnablingServiceVariant anEnablingServiceVariant:enablingServiceVariantList) {
                    enablingServiceVariantTabSet.add(anEnablingServiceVariant);
                }
            }
        }

        int esRowNum = 0;
        for (final EnablingService enablingService : enablingServiceTabSet) {
            generateDataRow(enabling_serviceSheet, enablingService.getESasXlsData(), ++esRowNum);
        }

        int esvRowNum = 0;
        for (final EnablingServiceVariant enablingServiceVariant : enablingServiceVariantTabSet) {
            generateDataRow(enabling_service_variantSheet, enablingServiceVariant.getESasXlsData(), ++esvRowNum);
        }
    }

    public void generateFinalXslFile() {
        try (FileOutputStream outputStream = new FileOutputStream(xlsFileName)) {
            workbook.write(outputStream);

        } catch (IOException exception) {
            //FIXME: logger
            exception.printStackTrace();
        } finally {
            closeResource(workbook);
        }

    }

    private void closeResource(final XSSFWorkbook workbook) {
        try {
            workbook.close();
        } catch (IOException e) {
            //FIXME: logger
            e.printStackTrace();
        }
    }



}
