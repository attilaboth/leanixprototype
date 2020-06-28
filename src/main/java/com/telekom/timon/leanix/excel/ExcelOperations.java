package com.telekom.timon.leanix.excel;

import com.telekom.timon.leanix.datamodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class ExcelOperations {
    private static Logger logger = LoggerFactory.getLogger(ExcelOperations.class);

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
        //FIXME: get from property file or global variable
        String MANDANT_NAME = "DTT";
        String SUBMANDANT_NAME = "DTT";
        String BUSINESS_FUNCTION_NAME = "mShop";

        //mandant tab
        XSSFSheet mandantSheet = createASheet("mandant");
        generateSheetHeader(mandantSheet, Arrays.asList("name"));
        generateDataRow(mandantSheet, Arrays.asList(MANDANT_NAME), 1);

        //submandant tab
        XSSFSheet submandantSheet = createASheet("submandant");
        generateSheetHeader(submandantSheet, Arrays.asList("name"));
        generateDataRow(submandantSheet, Arrays.asList(SUBMANDANT_NAME), 1);

        //business_function tab
        XSSFSheet business_functionSheet = createASheet("business_function");
        generateSheetHeader(business_functionSheet, Arrays.asList("name"));
        generateDataRow(business_functionSheet, Arrays.asList(BUSINESS_FUNCTION_NAME), 1);

        XSSFSheet business_activitySheet = createASheet("business_activity");
        generateSheetHeader(business_activitySheet, Arrays.asList("name", "id", "ibi_teilprozess", "network", "teilbereich", "teilprozess"));

        XSSFSheet enabling_serviceSheet = createASheet("enabling_service");
        generateSheetHeader(enabling_serviceSheet, Arrays.asList("name", "es_id"));
        Set<EnablingService> enablingServiceTabSet = new TreeSet<>();

        XSSFSheet enabling_service_variantSheet = createASheet("enabling_service_variant");
        generateSheetHeader(enabling_service_variantSheet, Arrays.asList("name", "implementation_id", "user_label", "description"));
        Set<EnablingServiceVariant> enablingServiceVariantTabSet = new TreeSet<>();

        XSSFSheet business_applicationSheet = createASheet("business_application");
        generateSheetHeader(business_applicationSheet, Arrays.asList("name"));
        Set<AppDarwinName> business_applicationSheetTabSet = new TreeSet<>();

        XSSFSheet relationshipsSheet = createASheet("relationships");
        generateSheetHeader(relationshipsSheet, Arrays.asList("start", "relation_type", "end"));
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
                //submandant-->relationship
                relationshipsTabList.add(new RelationshipUCMDB(SUBMANDANT_NAME,
                        BUSINESS_FUNCTION_NAME));

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
                        System.out.println(anEnablingServiceVariant.getEnablingServiceVariantName() + "--> " + appDarwinName.getDarwinName());
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

    public List<ArrayList<String>> readColumnsFromExcel(String excelName, List<Integer> columnNumbers) {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(excelName).getFile());
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


    public Map<String, List<String>> getSpecificColumnsBySheetName(String sheetName, int columnNumberAsKey,
                                                                   int columnNumbersAsValue, boolean isDarwinName) {

        Map<String, List<String>> validColumns = new TreeMap<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream(xlsFileName))) {

            int sheetNumber = 0;

            if (isValidSheetName(workbook, sheetName)) {
                sheetNumber = getSheetNumberFromSheetName(workbook, sheetName);
            } else {
                logger.error("'" + sheetName + "'" + " is an invalid sheet name!");
                return validColumns;
            }

            XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
            Iterator<Row> rowIt = sheet.iterator();

            while (rowIt.hasNext()) {
                Row row = rowIt.next();

                if (row.getCell(columnNumberAsKey - 1) != null && row.getCell(columnNumbersAsValue - 1) != null) {
                    String key = CellType.NUMERIC.equals(row.getCell(columnNumberAsKey - 1).getCellType()) ?
                            String.valueOf(row.getCell(columnNumberAsKey - 1).getNumericCellValue()).trim() :
                            row.getCell(columnNumberAsKey - 1).getStringCellValue().trim();

                    String value = CellType.NUMERIC.equals(row.getCell(columnNumbersAsValue - 1).getCellType()) ?
                            String.valueOf(row.getCell(columnNumbersAsValue - 1).getNumericCellValue()).trim() :
                            row.getCell(columnNumbersAsValue - 1).getStringCellValue().trim();

                        String capeName = CellType.NUMERIC.equals(row.getCell(columnNumbersAsValue - 2).getCellType()) ?
                                String.valueOf(row.getCell(columnNumbersAsValue - 2).getNumericCellValue()).trim() :
                                row.getCell(columnNumbersAsValue - 2).getStringCellValue().trim();


                    //NOTE: this was the problem with adding empty Strings, because they were not empty in fact, they
                    // had a value as 1 space (" "), so we have to trim() them before evaluating
                    if (key.isEmpty() || value.isEmpty()) {
                        //System.out.println(key + " ----> " + value);
                    }

                    if (!key.isEmpty() && !value.isEmpty()) {
                        //FIXME: we need names like this too
                        // ReO	REO_WIRK (GER004441)	ESV-00209
                        //if (!isDarwinName || (isDarwinName && value.contains("(P)"))) {

                        List<String> keyList = new ArrayList<>();

                        if (validColumns.get(key) != null) {
                            keyList = validColumns.get(key);
                        }

                        if(isDarwinName && !capeName.isEmpty()){
                            keyList.add(capeName + " | " +value);
                        }else{
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
        return validColumns;
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

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(xlsFileName).getFile());
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
