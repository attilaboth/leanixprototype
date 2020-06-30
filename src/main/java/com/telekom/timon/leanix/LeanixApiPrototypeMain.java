package com.telekom.timon.leanix;

import com.telekom.timon.leanix.datamodel.*;
import com.telekom.timon.leanix.excel.ExcelOperations;
import com.telekom.timon.leanix.leanixapi.GraphqlApiLeanix;
import com.telekom.timon.leanix.util.IOUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.telekom.timon.leanix.leanixapi.LeanixPototypeConstants.*;

public class LeanixApiPrototypeMain {

    //FIXME : remove when in prod
    //private static final String GENERATTED_FILE_NAME = "xlsFiles//bcc_generated.xlsx";
    public static final String GENERATTED_FILE_NAME = "c://DEV//bcc_generated.xlsx";
    private static boolean openGeneratedFile = false;
    private static final String CONTAINMENT = " | --> | ";

    private static IOUtil ioUtilInstace = new IOUtil();
    private static final Map<String, String> testPDFData = new HashMap<>();
    private static final GraphqlApiLeanix graphqlApiLeanix = new GraphqlApiLeanix();
    private static Map<String, List<String>> businessApplIdsMap; // FIXME: don't use static--- GC problem
    private static Map<String, List<String>> darwinNamesMap; //FIXME: don't use static--- GC problem
    private static final Map<String, BusinessActivity> dataToConvertToXls = new HashMap<>();

    static {

        testPDFData.put("1f929fba-8232-485e-b3e0-a99cb6659718",
                "PVG_TS-0001: Auftragsmanagement - MF - Bereitstellung - Neugeschäft PK");
        testPDFData.put("c8d9a47a-5c55-46c3-b4b5-6ad826f51b03",
                "PVG_TS-0002: Auftragsmanagement - MF - Bereitstellung - Bestandsgeschäft PK");
        /*
        testPDFData.put("fe984adc-bb4b-4449-bdbd-7132be2ed1fc",
            "PVG_TS-0005: Auftragsmanagement - FN - Bereitstellung - Produktbereitstellung");
        testPDFData.put("ea8c9aa9-7227-4d20-8cb1-53d4199e0665",
           "PVG_TS-0006: Auftragsmanagement - FN - Bereitstellung - Produktwechsel");
*/
        //FIXME: cache it upon startup, or use sort to find the BA_ID faster
        businessApplIdsMap = new ExcelOperations(APPLICATION_NAMES_WITH_BA_XLSX)
                .getSpecificColumnsBySheetName("Worksheet", 3, 5, false);

        //FIXME: cache it upon startup, or use sort to find the BA_ID faster
        darwinNamesMap = new ExcelOperations(DARWIN_NAMES_MAPPING_TABLE_XLSX)
                .getSpecificColumnsBySheetName("Application Role", 3, 2, true);
    }

    public static void main(String[] args) {

        final List<BusinessActivity> businessActivityList = new ArrayList<>();

        //FIXME: make it reading from a PROP file, make it nice
        settingProxy();

        gatherUserInput();

        testPDFData.forEach((leanixid, businessActivityName) -> {
            System.out.println("\nbusinessActivityName: " + businessActivityName + " with \n\t" + leanixid);
            businessActivityList.add(getBusinessActivityFromLeanixApi(
                    businessActivityName,
                    leanixid));
        });

        ///////////////////// ExcelOperations ////////////////////////////

        ExcelOperations excelWritter = new ExcelOperations(GENERATTED_FILE_NAME);
        excelWritter.generateDataFromObject(businessActivityList);
        excelWritter.generateFinalXslFile(openGeneratedFile);


    }

    private static void gatherUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Open generated file (Y/N)?: ");

            if (scanner.nextLine().equalsIgnoreCase("y")) {
                openGeneratedFile = true;
            }
        }
    }

    //FIXME: USe Optional
    private static BusinessActivity getBusinessActivityFromLeanixApi(final String businessActivityNameFromPDF,
                                                                     final String leanixIdFromH2DB) {
        return queryDataAndInstantiateBusinessActivity(businessActivityNameFromPDF,
                leanixIdFromH2DB);
    }

    private static void settingProxy() {
        try {
            Properties systemSettings = System.getProperties();
            systemSettings.put("proxySet", "true");
            systemSettings.put("https.proxyHost", "HE202194.emea2.cds.t-internal.com");
            systemSettings.put("https.proxyPort", "3128");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printContentToConsole(final BusinessActivity businessActivity) {
        System.out.println("business_activity tab: \n" + businessActivity.getBusinessActivityName());

        Set<EnablingService> enablingServiceSet = new TreeSet<>(businessActivity.getEnablingServiceList());
        Set<EnablingServiceVariant> enablingServiceVariantSet = new TreeSet<>();
        Set<AppDarwinName> businessApplicationNamesSet = new TreeSet<>();

        System.out.println("\nenabling_service tab: ");
        enablingServiceSet.forEach(enablingService -> {
            enablingServiceVariantSet.addAll(enablingService.getEnablingServiceVariantList());
            System.out.println(enablingService.getEnablingServiceName());
        });

        System.out.println("\nenabling_service_variant tab: ");
        enablingServiceVariantSet.forEach(enablingServiceVariant -> {
            businessApplicationNamesSet.addAll(enablingServiceVariant.getAppDarwinNameList());
            System.out.println(enablingServiceVariant.getEnablingServiceVariantName());
        });

        System.out.println("\nbusiness_application tab: ");
        businessApplicationNamesSet.forEach(appDarwinName -> {
            System.out.println(appDarwinName.getAppName());
        });

        System.out.println("\nrelationships tab: ");
        //business_activity --> enabling_service
        businessActivity.getEnablingServiceList().forEach(enablingService -> {
            System.out.println("\n(1)business_activity --> enabling_service::");
            String esName = enablingService.getEnablingServiceName();
            System.out.println(businessActivity.getBusinessActivityName() + CONTAINMENT + esName);
            //enabling_service --> enabling_service_variant

            System.out.println("\t(2)enabling_service --> enabling_service_variant::");
            enablingService.getEnablingServiceVariantList().forEach(enablingServiceVariant -> {
                String esvName = enablingServiceVariant.getEnablingServiceVariantName();
                System.out.println("\t" + esName + CONTAINMENT + esvName);
                //enabling_service_variant --> business_application
                System.out.println("\t\t(3)enabling_service_variant --> business_application::");
                enablingServiceVariant.getAppDarwinNameList().forEach(appDarwinName -> {
                    System.out.println("\t\t" + esvName + CONTAINMENT + appDarwinName.getAppName());
                });
            });
        });
    }

    //FIXME: refactor this is too long of a method
    private static BusinessActivity queryDataAndInstantiateBusinessActivity(String nameFromPDF, String leanixIDFromH2DB) {
        /***********************1st graphql Query goes directly to the H2 DB****************************/

        String fileContentFromResourceStreamBufferedReader =
                ioUtilInstace.getFileContentFromResourceStreamBufferedReader(
                        BUSINESS_CAPABILITY_CATALOGUE_GRAPHQL);
        /*graphqlApiLeanix.setGraphqlQueryString(
               IOUtil.getFileContentAsString("src/main/resources/graphql/BusinessCapabilityCatalogue.graphql"));
*/
        graphqlApiLeanix.setGraphqlQueryString(fileContentFromResourceStreamBufferedReader);

        Map<String, Map<String, Object>> businessCapabilityCatalogueData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> businessCatalogueSet = parseResultDataForSegment(businessCapabilityCatalogueData, "relToChild");

        System.out.print("Constructing data structure for " + nameFromPDF);

        BusinessActivity businessActivity = new BusinessActivity(leanixIDFromH2DB, nameFromPDF);
        businessActivity.setBusinessActivityExternalId(businessApplIdsMap.get(nameFromPDF).get(0));


        /***********************2nd graphql Query ****************************/
        //FIXME: cache result of ths call too
        String esvQuery = ioUtilInstace.getFileContentFromResourceStreamBufferedReader(
                ENABLING_SERVICE_VARIANT_GRAPHQL);
        String queryWithLeanixID = StringUtils.replaceOnce(esvQuery, "<leanixID>", leanixIDFromH2DB);
        graphqlApiLeanix.setGraphqlQueryString(queryWithLeanixID);

        Map<String, Map<String, Object>> enablingServiceVariantQueryData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> enablingServiceVariants = parseResultDataForSegment(enablingServiceVariantQueryData, "relBusinessCapabilityToProcess");
        List<EnablingServiceVariant> esvList = new ArrayList<>();

        enablingServiceVariants.forEach(esv -> {
            esvList.add(new EnablingServiceVariant(esv.getLeanixId(), esv.getName()));
        });
        //System.out.println("There are " + esvList.size() + " ESV-s for " + nameFromPDF);

        for (EnablingServiceVariant anEnablingServiceVariant : esvList) {

            /***********************3rd graphql Query****************************/
            String enablingServiceVariant = anEnablingServiceVariant.getEnablingServiceVariantName();

            String appForEs = ioUtilInstace.getFileContentFromResourceStreamBufferedReader(
                    APPLICATION_OF_ENABLING_SERVICE_GRAPHQL);

            String appForEsLeainxQuery = StringUtils.replaceOnce(appForEs, "<leanixID>", anEnablingServiceVariant.getEsvLeanixId());
            graphqlApiLeanix.setGraphqlQueryString(appForEsLeainxQuery);

            Map<String, Map<String, Object>> applicationsForESV = graphqlApiLeanix.executeQuery();
            Set<ResultObject> applicationNamesSet = parseResultDataForSegment(applicationsForESV, "relProcessToApplication");
            Set<ResultObject> enablingServicesSet = parseResultDataForSegment(applicationsForESV, "relToParent");

            enablingServicesSet.forEach(es -> {
                EnablingService enablingService = new EnablingService(es.getLeanixId(), es.getName());
                businessActivity.getEnablingServiceList().add(enablingService);
                //System.out.println("\tES: " + es.getName());
                applicationNamesSet.forEach(app -> {
                    //System.out.println("app: " + app.getName() + " ::: " + anEnablingServiceVariant
                    // .getEnablingServiceVariantName());
                    AppDarwinName appDarwinName = new AppDarwinName(app.getName());
                    List<String> possibleApplNamesList = darwinNamesMap.get(anEnablingServiceVariant.getEvsId());

                    appDarwinName.getDarwinNameList().addAll(possibleApplNamesList);
                    appDarwinName.setDarwinName(appDarwinName.replaceApplicationNameWithDarwinName()); //TODO: make

                    anEnablingServiceVariant.getAppDarwinNameList().add(appDarwinName);
                });
                enablingService.getEnablingServiceVariantList().add(anEnablingServiceVariant);
            });
            System.out.println();

        }
        return businessActivity;
    }

    private static Set<ResultObject> parseResultDataForSegment(final Map<String, Map<String, Object>> enablingServiceVariantQueryData,
                                                               String nameOfSegment) {
        Set<ResultObject> esvSet = new HashSet<>();

        Map<String, Object> factSheet = enablingServiceVariantQueryData.get("factSheet");
        Map<String, Object> relBusinessCapabilityToProcess = (Map<String, Object>) factSheet.get(nameOfSegment);
        List<Object> edgesList = (List<Object>) relBusinessCapabilityToProcess.get("edges");
        for (final Object edge : edgesList) {
            Map<String, Object> edgesMap = (Map<String, Object>) edge;
            Map<String, Object> nodeMap = (Map<String, Object>) edgesMap.get("node");
            Map<String, Object> factSheetMap = (Map<String, Object>) nodeMap.get("factSheet");

            String name = (String) factSheetMap.get("name");
            String displayName = (String) factSheetMap.get("displayName");
            String description = (String) factSheetMap.get("description");
            //FIXME: remove if not used, but description has to be constructed manually
            if (null != description) {
                System.out.println("description: " + description);
            }
            // this?
            String leanixId = (String) factSheetMap.get("id");
            esvSet.add(new ResultObject(name, displayName, leanixId, description));
        }
        return esvSet;
    }

}

