package com.telekom.timon.leanix.excel;

import com.telekom.timon.leanix.datamodel.BusinessActivity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

        for (BusinessActivity aBusinessActivity: businessActivityList) {


            XSSFSheet relationshipsSheet = createASheet("business_activity");
            generateSheetHeader(relationshipsSheet, Arrays.asList("name", "id", "ibi_teilprozess","network","teilbereich","teilprozess"));
            generateDataRow(relationshipsSheet,aBusinessActivity.getBaAsXlsData(),1);


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
