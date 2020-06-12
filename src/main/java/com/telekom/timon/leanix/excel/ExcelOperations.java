package com.telekom.timon.leanix.excel;

import com.telekom.timon.leanix.datamodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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
        Set<EnablingService> enablingServiceTabSet = new TreeSet<>();

        XSSFSheet enabling_service_variantSheet = createASheet("enabling_service_variant");
        generateSheetHeader(enabling_service_variantSheet, Arrays.asList("name", "implementation_id", "user_label", "description"));
        Set<EnablingServiceVariant> enablingServiceVariantTabSet = new TreeSet<>();

        XSSFSheet business_applicationSheet = createASheet("business_application");
        generateSheetHeader(business_applicationSheet, Arrays.asList("name", "DARWIN_NAME"));
        Set<AppDarwinName> business_applicationSheetTabSet = new TreeSet<>();

        XSSFSheet relationshipsSheet = createASheet("relationships");
        generateSheetHeader(relationshipsSheet, Arrays.asList("start", "relation_type", "end"));
        List<RelationshipUCMDB> relationshipsTabList = new ArrayList<>();

        int baRowNum = 0;
        for (BusinessActivity aBusinessActivity: businessActivityList) {

            //business_activity tab
            generateDataRow(business_activitySheet,aBusinessActivity.getBaAsXlsData(), ++baRowNum);

            //enabling_service tab
            for (EnablingService anEnablingService: aBusinessActivity.getEnablingServiceList()) {
                enablingServiceTabSet.add(anEnablingService);

                //relationships: business_activity --> enabling_service
                relationshipsTabList.add(new RelationshipUCMDB(aBusinessActivity.getBusinessActivityName(),
                        anEnablingService.getEnablingServiceName()));

                //enabling_service_variant tab
                final List<EnablingServiceVariant> enablingServiceVariantList = anEnablingService.getEnablingServiceVariantList();
                for (EnablingServiceVariant anEnablingServiceVariant:enablingServiceVariantList) {
                    enablingServiceVariantTabSet.add(anEnablingServiceVariant);

                    //relationships: enabling_service --> enabling_service_variant
                    relationshipsTabList.add(new RelationshipUCMDB(anEnablingService.getEnablingServiceName(),
                            anEnablingServiceVariant.getEnablingServiceVariantName()));


                    //business_application tab
                    List<AppDarwinName> appDarwinNameList = anEnablingServiceVariant.getAppDarwinNameList();
                    for (AppDarwinName appDarwinName:appDarwinNameList) {
                        business_applicationSheetTabSet.add(appDarwinName);

                        //relationships: enabling_service_variant --> business_application
                        System.out.println(anEnablingServiceVariant.getEnablingServiceVariantName() +  "--> " + appDarwinName.getDarwinName());
                        relationshipsTabList.add(new RelationshipUCMDB(anEnablingServiceVariant.getEnablingServiceVariantName(),
                                appDarwinName.getDarwinName()));
                    }
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

        int appNameRowNum = 0;
        for (final AppDarwinName appDarwinName : business_applicationSheetTabSet) {
            generateDataRow(business_applicationSheet, appDarwinName.getDarwinNameAsXslData(), ++appNameRowNum);
        }

        int relationsRowNum = 0;
        Set<RelationshipUCMDB> relationshipUCMDBSet = new HashSet<>();
        relationshipUCMDBSet.addAll(relationshipsTabList);
        for (final RelationshipUCMDB aRelationship : relationshipUCMDBSet) {
            generateDataRow(relationshipsSheet, aRelationship.getUCMDBAsXlsData(), ++relationsRowNum);
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
