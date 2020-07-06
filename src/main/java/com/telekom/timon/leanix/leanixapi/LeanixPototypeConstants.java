package com.telekom.timon.leanix.leanixapi;

public class LeanixPototypeConstants {

    public static final String APPLICATION_NAMES_WITH_BA_XLSX = "/xlsFilesToBeParsed/ApplicationNamesWithBA_ids.xlsx";
    public static final String DARWIN_NAMES_MAPPING_TABLE_XLSX = "/xlsFilesToBeParsed/DarwinNames_itcoNum_applicationNames.xlsx";
    public static final String GENERATED_BBC_EXCEL_XLSX = "src/main/resources/generatedFiles/BCC.xls";

    public static final String OUTPUT_DIR = "C://DEV//";
    public static final String GENERATTED_XLSX_FILE_NAME = OUTPUT_DIR + "bcc_generated.xlsx";
    public static final String GENERATTED_PERFORMANCE_RESULT = OUTPUT_DIR + "methodPerformances.txt";
    public static final String GRAPHQL_QUERY_EDGES = "edges";

    public static final String BUSINESS_CAPABILITY_CATALOGUE_GRAPHQL = "/graphql/BusinessCapabilityCatalogue.graphql";
    public static final String ENABLING_SERVICE_VARIANT_GRAPHQL = "/graphql/EnablingServiceVariantQuery.graphql";
    public static final String APPLICATION_OF_ENABLING_SERVICE_GRAPHQL = "/graphql/ApplicationsForES.graphql";

    public static final String LEAN_IX_ID = "<leanixID>";
    public static final String GRAPHQL_QUERY_RELATION_TO_CHILD = "relToChild";
    public static final String GRAPHQL_QUERY_RELATION_TO_PARENT = "relToParent";
    public static final String GRAPHQL_QUERY_RELATION_BUSINESS_CAPABILITY_TO_PROCESS = "relBusinessCapabilityToProcess";
    public static final String GRAPHQL_QUERY_RELATION_PROCESS_TO_APPLICATION = "relProcessToApplication";
    public static final String GRAPHQL_QUERY_FACT_SHEET = "factSheet";
    public static final String GRAPHQL_QUERY_NODE = "node";
    public static final String GRAPHQL_QUERY_NAME = "name";
    public static final String GRAPHQL_QUERY_ID = "id";
    public static final String GRAPHQL_QUERY_DISPLAY_NAME = "displayName";
    public static final String GRAPHQL_QUERY_DESCRIPTION = "description";

    public static final String XLSX_SHEET_NAME_MANDANT = "mandant";
    public static final String XLSX_SHEET_NAME_SUBMANDANT = "submandant";
    public static final String XLSX_SHEET_NAME_BUSINESS_FUNCTION = "business_function";
    public static final String XLSX_SHEET_NAME_BUSINESS_ACTIVITY = "business_activity";
    public static final String XLSX_SHEET_NAME_ENABLING_SERVICE = "enabling_service";
    public static final String XLSX_SHEET_NAME_ENABLING_SERVICE_VARIANT = "enabling_service_variant";
    public static final String XLSX_SHEET_NAME_BUSINESS_APPLICATION = "business_application";
    public static final String XLSX_SHEET_NAME_RELATIONSHIPS = "relationships";

    public static final String XLSX_SHEET_COLUMN_NAME_NAME = "name";
    public static final String XLSX_SHEET_COLUMN_NAME_ID = "id";
    public static final String XLSX_SHEET_COLUMN_NAME_IBI_TEILPROZESS = "ibi_teilprozess";
    public static final String XLSX_SHEET_COLUMN_NAME_NETWORK = "network";
    public static final String XLSX_SHEET_COLUMN_NAME_TEILBEREICH = "teilbereich";
    public static final String XLSX_SHEET_COLUMN_NAME_TEILPROZESS = "teilprozess";
    public static final String XLSX_SHEET_COLUMN_NAME_ES_ID = "es_id";
    public static final String XLSX_SHEET_COLUMN_NAME_IMPLEMENTATION_ID = "implementation_id";
    public static final String XLSX_SHEET_COLUMN_NAME_USER_LABEL = "user_label";
    public static final String XLSX_SHEET_COLUMN_NAME_DESCRIPTION = "description";
    public static final String XLSX_SHEET_COLUMN_NAME_START = "start";
    public static final String XLSX_SHEET_COLUMN_NAME_RELATION_TYPE = "relation_type";
    public static final String XLSX_SHEET_COLUMN_NAME_END = "end";

    private LeanixPototypeConstants() {
    }

}


