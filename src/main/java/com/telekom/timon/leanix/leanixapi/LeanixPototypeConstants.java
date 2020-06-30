package com.telekom.timon.leanix.leanixapi;

public class LeanixPototypeConstants {

    private LeanixPototypeConstants(){}

    public static final String APPLICATION_NAMES_WITH_BA_XLSX = "/xlsFilesToBeParsed/ApplicationNamesWithBA_ids.xlsx";
    public static final String DARWIN_NAMES_MAPPING_TABLE_XLSX = "/xlsFilesToBeParsed/DarwinNames_itcoNum_applicationNames.xlsx";

    public static final String BUSINESS_CAPABILITY_CATALOGUE_GRAPHQL = "/graphql/BusinessCapabilityCatalogue.graphql";
    public static final String ENABLING_SERVICE_VARIANT_GRAPHQL = "/graphql/EnablingServiceVariantQuery.graphql";
    public static final String APPLICATION_OF_ENABLING_SERVICE_GRAPHQL = "/graphql/ApplicationsForES.graphql";

    //FIXME: read from property file upon startup
    public static final String OUTPUT_DIR = "C://DEV//";
    public static final String GENERATTED_XLSX_FILE_NAME = OUTPUT_DIR+"bcc_generated.xlsx";
    public static final String GENERATTED_PERFORMANCE_RESULT = OUTPUT_DIR+"methodPerformances.txt";


}
