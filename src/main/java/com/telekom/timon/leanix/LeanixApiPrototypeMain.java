package com.telekom.timon.leanix;

import com.telekom.timon.leanix.datamodel.*;
import com.telekom.timon.leanix.excel.ExcelOperations;
import com.telekom.timon.leanix.leanixapi.GraphqlApiLeanix;
import com.telekom.timon.leanix.performance.PerformanceTester;
import com.telekom.timon.leanix.util.IOUtil;
import com.telekom.timon.leanix.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;

import static com.telekom.timon.leanix.leanixapi.LeanixPototypeConstants.*;
public class LeanixApiPrototypeMain {

    private static final Map<String, String> testPDFData = new HashMap<>();
    private static final String CONTAINMENT = " | --> | ";
    private static final GraphqlApiLeanix graphqlApiLeanix = new GraphqlApiLeanix();
    private static final Map<String, BusinessActivity> dataToConvertToXls = new HashMap<>();
    //FIXME : remove when in prod
    private static IOUtil ioUtilInstace = new IOUtil();
    private static PerformanceTester performanceWriter = new PerformanceTester();
    private static PropertiesUtil props = new PropertiesUtil();
    private static Map<String, List<String>> businessApplIdsMap; // FIXME: don't use static--- GC problem
    private static Map<String, List<String>> darwinNamesMap; //FIXME: don't use static--- GC problem

    static {
/*
        testPDFData.put("1f929fba-8232-485e-b3e0-a99cb6659718",
                "PVG_TS-0001: Auftragsmanagement - MF - Bereitstellung - Neugeschäft PK");
        testPDFData.put("c8d9a47a-5c55-46c3-b4b5-6ad826f51b03",
                "PVG_TS-0002: Auftragsmanagement - MF - Bereitstellung - Bestandsgeschäft PK");
        /*testPDFData.put("fe984adc-bb4b-4449-bdbd-7132be2ed1fc",
            "PVG_TS-0005: Auftragsmanagement - FN - Bereitstellung - Produktbereitstellung");
        testPDFData.put("ea8c9aa9-7227-4d20-8cb1-53d4199e0665",
           "PVG_TS-0006: Auftragsmanagement - FN - Bereitstellung - Produktwechsel");*/

        //FIXME: cache it upon startup, or use sort to find the BA_ID faster
        businessApplIdsMap = new ExcelOperations("/xlsFilesToBeParsed/ApplicationNamesWithBA_ids.xlsx")
                .getSpecificColumnsBySheetName("Worksheet", 3, 5, false);

        //FIXME: cache it upon startup, or use sort to find the BA_ID faster
        darwinNamesMap = new ExcelOperations("/xlsFilesToBeParsed" +
                "/DarwinNames_itcoNum_applicationNames.xlsx")
                .getSpecificColumnsBySheetName("Application Role", 3, 2, true);
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        /*Properties proxySettings = props.getProperties("proxySettings.properties");
        Properties systemSettings = System.getProperties();
        systemSettings.setProperty("proxySet", proxySettings.getProperty("proxySet"));
        systemSettings.setProperty("https.proxyHost", proxySettings.getProperty("https.proxyHost"));
        systemSettings.setProperty("https.proxyPort", proxySettings.getProperty("https.proxyPort"));*/
        settingProxy();

        Set<ResultObject> allCapabilitiesSet = getCapabilityCatalogue();

        final List<BusinessActivity> businessActivityList = new ArrayList<>();

        List<String> businessActivitiesList = Arrays.asList(
                "PVG_TS-0001: Auftragsmanagement - MF - Bereitstellung - Neugeschäft PK",
                "PVG_TS-0002: Auftragsmanagement - MF - Bereitstellung - Bestandsgeschäft PK",
                "PVG_TS-0005: Auftragsmanagement - FN - Bereitstellung - Produktbereitstellung",
                "PVG_TS-0006: Auftragsmanagement - FN - Bereitstellung - Produktwechsel");

        businessActivitiesList.forEach((businessActivityName) -> {
            String prefixOnly = businessActivityName.substring(0,businessActivityName.indexOf(": "));
            System.out.println("prefixOnly: " + prefixOnly);

            ResultObject foundCapability = allCapabilitiesSet.stream()
                    .filter(aCapability -> aCapability.getPrefixOnly().equalsIgnoreCase(prefixOnly.trim()))
                    .findAny()
                    .orElse(null);

            if (null != foundCapability){
                businessActivityList.add(getBusinessActivityFromLeanixApi(
                        foundCapability.getName(),
                        foundCapability.getLeanixId()));
                System.out.println("businessActivityName: " + foundCapability.getName());
            }
        });

        ///////////////////// ExcelOperations ////////////////////////////

        ExcelOperations excelWriter = new ExcelOperations(GENERATTED_XLSX_FILE_NAME);
        excelWriter.generateDataFromObject(businessActivityList);
        excelWriter.generateFinalXslFile(true);

        performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());
        performanceWriter.closePerformanceWriter();
    }

    private static void settingProxy() {
        //-Dhttps.proxyHost=HE202194.emea2.cds.t-internal.com -Dhttps.proxyPort=3128
        try {
            Properties systemSettings = System.getProperties();
            systemSettings.put("proxySet", "true");
            systemSettings.put("https.proxyHost", "HE202194.emea2.cds.t-internal.com");
            systemSettings.put("https.proxyPort", "3128");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //FIXME: USe Optional
    private static BusinessActivity getBusinessActivityFromLeanixApi(final String businessActivityNameFromPDF,
                                                                     final String leanixIdFromH2DB) {
        Instant start = Instant.now();

        BusinessActivity businessActivity = queryDataAndInstantiateBusinessActivity(businessActivityNameFromPDF, leanixIdFromH2DB);

        performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());

        return businessActivity;
    }



    //FIXME: refactor this is too long of a method
    private static BusinessActivity queryDataAndInstantiateBusinessActivity(String nameFromPDF, String leanixIDFromH2DB) {
        Instant start = Instant.now();

        //System.out.print("Constructing data structure for " + nameFromPDF);
        //System.out.println("\t " + leanixIDFromH2DB);

        BusinessActivity businessActivity = new BusinessActivity(leanixIDFromH2DB, nameFromPDF);
        businessActivity.setBusinessActivityExternalId(businessApplIdsMap.get(nameFromPDF).get(0));

        /***********************2nd graphql Query ****************************/
        String esvQuery = IOUtil.getFileContentFromResourceStreamBufferedReader(ENABLING_SERVICE_VARIANT_GRAPHQL);
        graphqlApiLeanix.setGraphqlQueryString(StringUtils.replaceOnce(esvQuery, LEAN_IX_ID, leanixIDFromH2DB));

        Map<String, Map<String, Object>> enablingServiceVariantQueryData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> enablingServiceVariants = parseResultDataForSegment(enablingServiceVariantQueryData,
                GRAPHQL_QUERY_RELATION_BUSINESS_CAPABILITY_TO_PROCESS);

        List<EnablingServiceVariant> esvList = new ArrayList<>();
        enablingServiceVariants.forEach(esv -> {
            esvList.add(new EnablingServiceVariant(esv.getLeanixId(), esv.getName()));
        });

        for (EnablingServiceVariant anEnablingServiceVariant : esvList) {

            /***********************3rd graphql Query****************************/
            String appForEs = IOUtil.getFileContentFromResourceStreamBufferedReader(APPLICATION_OF_ENABLING_SERVICE_GRAPHQL);
            graphqlApiLeanix.setGraphqlQueryString(StringUtils.replaceOnce(appForEs, LEAN_IX_ID, anEnablingServiceVariant.getEsvLeanixId()));

            Map<String, Map<String, Object>> applicationsForESV = graphqlApiLeanix.executeQuery();
            Set<ResultObject> applicationNamesSet = parseResultDataForSegment(applicationsForESV, GRAPHQL_QUERY_RELATION_PROCESS_TO_APPLICATION);
            Set<ResultObject> enablingServicesSet = parseResultDataForSegment(applicationsForESV, GRAPHQL_QUERY_RELATION_TO_PARENT);

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

                    appDarwinName.setDarwinName(appDarwinName.replaceApplicationNameWithDarwinName());

                    anEnablingServiceVariant.getAppDarwinNameList().add(appDarwinName);
                });
                enablingService.getEnablingServiceVariantList().add(anEnablingServiceVariant);
            });
        }
        performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());
        return businessActivity;
    }

    private static Set<ResultObject> getCapabilityCatalogue() {
        String fileContentFromResourceStreamBufferedReader =
                ioUtilInstace.getFileContentFromResourceStreamBufferedReader(BUSINESS_CAPABILITY_CATALOGUE_GRAPHQL);

        graphqlApiLeanix.setGraphqlQueryString(fileContentFromResourceStreamBufferedReader);

        Map<String, Map<String, Object>> businessCapabilityCatalogueData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> businessCatalogueSet = parseResultDataForSegment(businessCapabilityCatalogueData, GRAPHQL_QUERY_RELATION_TO_CHILD);

        return businessCatalogueSet;
    }


    private static Set<ResultObject> parseResultDataForSegment(final Map<String, Map<String, Object>> enablingServiceVariantQueryData,
                                                               String nameOfSegment) {
        Instant start = Instant.now();
        Set<ResultObject> esvSet = new HashSet<>();

        Map<String, Object> factSheet = enablingServiceVariantQueryData.get(GRAPHQL_QUERY_FACT_SHEET);
        Map<String, Object> relBusinessCapabilityToProcess = (Map<String, Object>) factSheet.get(nameOfSegment);
        List<Object> edgesList = (List<Object>) relBusinessCapabilityToProcess.get(GRAPHQL_QUERY_EDGES);

        for (final Object edge : edgesList) {
            Map<String, Object> edgesMap = (Map<String, Object>) edge;
            Map<String, Object> nodeMap = (Map<String, Object>) edgesMap.get(GRAPHQL_QUERY_NODE);
            Map<String, Object> factSheetMap = (Map<String, Object>) nodeMap.get(GRAPHQL_QUERY_FACT_SHEET);

            String name = (String) factSheetMap.get(GRAPHQL_QUERY_NAME);
            String displayName = (String) factSheetMap.get(GRAPHQL_QUERY_DISPLAY_NAME);
            String description = (String) factSheetMap.get(GRAPHQL_QUERY_DESCRIPTION);
            String leanixId = (String) factSheetMap.get(GRAPHQL_QUERY_ID);

            esvSet.add(new ResultObject(name, displayName, leanixId, description));
        }

        //performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());
        return esvSet;
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
}

