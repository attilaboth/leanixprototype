package com.telekom.timon.leanix.excel;

import com.telekom.timon.leanix.datamodel.*;
import com.telekom.timon.leanix.performance.PerformanceTester;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.*;

import static com.telekom.timon.leanix.leanixapi.LeanixPototypeConstants.*;

public class ExcelOperations {

    private final XSSFWorkbook workbook;
    private final String xlsFileName;

    private final PerformanceTester performanceWriter = new PerformanceTester();

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
        Instant start = Instant.now();

        //FIXME: get from property file or global variable
        String MANDANT_NAME = "DTT";
        String SUBMANDANT_NAME = "DTT";
        String BUSINESS_FUNCTION_NAME = "mShop";

        //mandant tab
        XSSFSheet mandantSheet = createASheet(XLSX_SHEET_NAME_MANDANT);
        generateSheetHeader(mandantSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME));
        generateDataRow(mandantSheet, Arrays.asList(MANDANT_NAME), 1);

        //submandant tab
        XSSFSheet submandantSheet = createASheet(XLSX_SHEET_NAME_SUBMANDANT);
        generateSheetHeader(submandantSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME));
        generateDataRow(submandantSheet, Arrays.asList(SUBMANDANT_NAME), 1);

        //business_function tab
        XSSFSheet businessFunctionSheet = createASheet(XLSX_SHEET_NAME_BUSINESS_FUNCTION);
        generateSheetHeader(businessFunctionSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME));
        generateDataRow(businessFunctionSheet, Arrays.asList(BUSINESS_FUNCTION_NAME), 1);

        XSSFSheet business_activitySheet = createASheet(XLSX_SHEET_NAME_BUSINESS_ACTIVITY);
        generateSheetHeader(business_activitySheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME, XLSX_SHEET_COLUMN_NAME_ID,
                XLSX_SHEET_COLUMN_NAME_IBI_TEILPROZESS, XLSX_SHEET_COLUMN_NAME_NETWORK, XLSX_SHEET_COLUMN_NAME_TEILBEREICH,
                XLSX_SHEET_COLUMN_NAME_TEILPROZESS));

        XSSFSheet enabling_serviceSheet = createASheet(XLSX_SHEET_NAME_ENABLING_SERVICE);
        generateSheetHeader(enabling_serviceSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME, XLSX_SHEET_COLUMN_NAME_ES_ID));
        Set<EnablingService> enablingServiceTabSet = new TreeSet<>();

        XSSFSheet enabling_service_variantSheet = createASheet(XLSX_SHEET_NAME_ENABLING_SERVICE_VARIANT);
        generateSheetHeader(enabling_service_variantSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME,
                XLSX_SHEET_COLUMN_NAME_IMPLEMENTATION_ID, XLSX_SHEET_COLUMN_NAME_USER_LABEL, XLSX_SHEET_COLUMN_NAME_DESCRIPTION));
        Set<EnablingServiceVariant> enablingServiceVariantTabSet = new TreeSet<>();

        XSSFSheet business_applicationSheet = createASheet(XLSX_SHEET_NAME_BUSINESS_APPLICATION);
        generateSheetHeader(business_applicationSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_NAME));
        Set<AppDarwinName> business_applicationSheetTabSet = new TreeSet<>();

        XSSFSheet relationshipsSheet = createASheet(XLSX_SHEET_NAME_RELATIONSHIPS);
        generateSheetHeader(relationshipsSheet, Arrays.asList(XLSX_SHEET_COLUMN_NAME_START, XLSX_SHEET_COLUMN_NAME_RELATION_TYPE,
                XLSX_SHEET_COLUMN_NAME_END));
        List<RelationshipUCMDB> relationshipsTabList = new ArrayList<>();

        int baRowNum = 0;
        for (BusinessActivity aBusinessActivity : businessActivityList) {

            //business_activity tab
            generateDataRow(business_activitySheet, aBusinessActivity.getBaAsXlsData(), ++baRowNum);

            //enabling_service tab
            for (EnablingService anEnablingService : aBusinessActivity.getEnablingServiceList()) {
                enablingServiceTabSet.add(anEnablingService);

                //relationships:
                //mandant-->submandant
                relationshipsTabList.add(new RelationshipUCMDB(MANDANT_NAME,
                        SUBMANDANT_NAME));
                //submandant-->business function
                relationshipsTabList.add(new RelationshipUCMDB(SUBMANDANT_NAME,
                        BUSINESS_FUNCTION_NAME));

                //business function --> business_activity
                relationshipsTabList.add(new RelationshipUCMDB(BUSINESS_FUNCTION_NAME,
                        aBusinessActivity.getBusinessActivityName()));

                // business_activity --> enabling_service
                relationshipsTabList.add(new RelationshipUCMDB(aBusinessActivity.getBusinessActivityName(),
                        anEnablingService.getEnablingServiceName()));

                //enabling_service_variant tab
                final List<EnablingServiceVariant> enablingServiceVariantList = anEnablingService.getEnablingServiceVariantList();
                for (EnablingServiceVariant anEnablingServiceVariant : enablingServiceVariantList) {
                    enablingServiceVariantTabSet.add(anEnablingServiceVariant);

                    //relationships: enabling_service --> enabling_service_variant
                    relationshipsTabList.add(new RelationshipUCMDB(anEnablingService.getEnablingServiceName(),
                            anEnablingServiceVariant.getEnablingServiceVariantName()));


                    //business_application tab
                    List<AppDarwinName> appDarwinNameList = anEnablingServiceVariant.getAppDarwinNameList();
                    for (AppDarwinName appDarwinName : appDarwinNameList) {
                        business_applicationSheetTabSet.add(appDarwinName);

                        //relationships: enabling_service_variant --> business_application
                        //System.out.println(anEnablingServiceVariant.getEnablingServiceVariantName() + "--> " +
                        // appDarwinName.getDarwinName());

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

        //performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());
    }

    public void generateFinalXslFile(final boolean openGeneratedFile) {
        try (FileOutputStream outputStream = new FileOutputStream(xlsFileName)) {
            workbook.write(outputStream);
            if(openGeneratedFile){
                openGeneratedXlsFile();
            }
        } catch (IOException exception) {
            //FIXME: logger
            exception.printStackTrace();
        } finally {
            closeResource(workbook);
        }
    }

    public void openGeneratedXlsFile(){
        File file = new File(GENERATTED_XLSX_FILE_NAME);

        //first check if Desktop is supported by Platform or not
        if(Desktop.isDesktopSupported()){
            if(file.exists()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            //System.out.println("Desktop is not supported");
            return;
        }
    }

    public List<ArrayList<String>> readColumnsFromExcel(String excelName, List<Integer> columnNumbers) {

        File file = new File(getClass().getClassLoader().getResource(excelName).getFile());
        List<ArrayList<String>> validColumnContents = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);

                if (!sheet.getSheetName().equals("ReadMe")) {
                    Iterator<Row> rowIt = sheet.iterator();

                    while (rowIt.hasNext()) {
                        Row row = rowIt.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        ArrayList<String> rowContent = new ArrayList<>();

                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();

                            for (int j = 0; j < columnNumbers.size(); j++) {

                                if (cell.getColumnIndex() == columnNumbers.get(j) - 1) {
                                    rowContent.add(cell.getStringCellValue());
                                }
                            }
                        }
                        validColumnContents.add(rowContent);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return validColumnContents;
    }


    public Map<String, Set<String>> getSpecificColumnsBySheetName(String sheetName, int columnNumberAsKey,
                                                                  int columnNumbersAsValue, boolean isDarwinName) {
        Instant start = Instant.now();
        Map<String, Set<String>> validColumns = new TreeMap<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream(xlsFileName))) {

            int sheetNumber = 0;

            if (isValidSheetName(workbook, sheetName)) {
                sheetNumber = getSheetNumberFromSheetName(workbook, sheetName);
            } else {
                return validColumns;
            }

            XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
            Iterator<Row> rowIt = sheet.iterator();

            while (rowIt.hasNext()) {
                Row row = rowIt.next();

                if (row.getCell(columnNumberAsKey - 1) != null && row.getCell(columnNumbersAsValue - 1) != null) {

                    String key = getSpecificCellValue(row, columnNumberAsKey, 0);
                    String value = getSpecificCellValue(row, columnNumbersAsValue, 0);
                    String capeName = getSpecificCellValue(row, columnNumbersAsValue, 1);

                    if (!key.isEmpty() && !value.isEmpty()) {
                        Set<String> keyList = new TreeSet<>();

                        if (validColumns.get(key) != null) {
                            keyList = validColumns.get(key);
                        }

                        if (isDarwinName && !capeName.isEmpty()) {
                            keyList.add(capeName + " | " + value);
                            //System.out.println(capeName + " | " + value);
                        } else {
                            keyList.add(value);
                        }
                        validColumns.put(key, keyList);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());

        return validColumns;
    }

    private String getSpecificCellValue(Row row, int columnNumber, int columnDistanceFromTheSpecificColumn) {
        return CellType.NUMERIC.equals(row.getCell(columnNumber - 1 - columnDistanceFromTheSpecificColumn).getCellType()) ?
                String.valueOf(row.getCell(columnNumber - 1 - columnDistanceFromTheSpecificColumn).getNumericCellValue()).trim() :
                row.getCell(columnNumber - 1 - columnDistanceFromTheSpecificColumn).getStringCellValue().trim();
    }

    private boolean isValidSheetName(XSSFWorkbook workbook, String sheetName) {
        boolean matchFound = false;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (sheetName.equals(workbook.getSheetName(i))) {
                matchFound = true;
            }
        }

        return matchFound;
    }

    private int getSheetNumberFromSheetName(XSSFWorkbook workbook, String sheetName) {
        int sheetNumber = 0;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (sheetName.equals(workbook.getSheetName(i))) {
                sheetNumber = i;
            }
        }
        return sheetNumber;
    }

    public List<List<ArrayList<String>>> readExcelFile() {

        File file = new File(getClass().getClassLoader().getResource(xlsFileName).getFile());
        List<List<ArrayList<String>>> excelContent = new ArrayList<>();

        //FIXME: workbookot megnézni, hogy lehet e osztályváltozót használni
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                List<ArrayList<String>> sheetContent = new ArrayList<>();
                XSSFSheet sheet = workbook.getSheetAt(i);

                if (!sheet.getSheetName().equals("ReadMe")) {
                    Iterator<Row> rowIt = sheet.iterator();

                    while (rowIt.hasNext()) {
                        Row row = rowIt.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        ArrayList<String> rowContent = new ArrayList<>();

                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();

                            if (cell != null && CellType.NUMERIC.equals(cell.getCellType())) {
                                rowContent.add(String.valueOf(cell.getNumericCellValue()));
                            } else {
                                rowContent.add(cell.getStringCellValue());
                            }
                        }
                        sheetContent.add(rowContent);
                    }
                }

                if (!sheetContent.isEmpty()) {
                    excelContent.add(sheetContent);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        return excelContent;
    }

    public Map<String, ArrayList<String>> getSpecificColumnsFromExcel(List<List<ArrayList<String>>> excelFile,
                                                                      int columnNumberAsKey, int sheetNumber,
                                                                      List<Integer> columnNumbers) {

        List<ArrayList<String>> sheetContent = excelFile.get(sheetNumber - 1);
        Map<String, ArrayList<String>> validColumns = new HashMap<>();

        for (ArrayList<String> row : sheetContent) {
            ArrayList<String> validRow = new ArrayList();

            for (int i = 0; i < columnNumbers.size(); i++) {

                if ((columnNumbers.get(i) - 1) != columnNumberAsKey) {
                    validRow.add(row.get(columnNumbers.get(i) - 1));
                }
            }
            validColumns.put(row.get(columnNumberAsKey - 1), validRow);
        }

        return validColumns;
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
