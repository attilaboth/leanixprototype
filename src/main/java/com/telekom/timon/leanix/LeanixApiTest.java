package com.telekom.timon.leanix;

import com.telekom.timon.leanix.datamodel.*;
import com.telekom.timon.leanix.excel.ExcelOperations;
import com.telekom.timon.leanix.leanixapi.GraphqlApiLeanix;
import com.telekom.timon.leanix.util.IOUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class LeanixApiTest {

    private static final Map<String, String> testPDFData = new HashMap<>();
    private static final String CONTAINMENT = " | --> | ";

    static {
        testPDFData.put("1f929fba-8232-485e-b3e0-a99cb6659718",
                "PVG_TS-0001: Auftragsmanagement - MF - Bereitstellung - Neugeschäft PK");
        testPDFData.put("c8d9a47a-5c55-46c3-b4b5-6ad826f51b03",
                "PVG_TS-0002: Auftragsmanagement - MF - Bereitstellung - Bestandsgeschäft PK");
        //testPDFData.put("fe984adc-bb4b-4449-bdbd-7132be2ed1fc",
        //      "PVG_TS-0005: Auftragsmanagement - FN - Bereitstellung - Produktbereitstellung");
        //testPDFData.put("ea8c9aa9-7227-4d20-8cb1-53d4199e0665",
        //      "PVG_TS-0006: Auftragsmanagement - FN - Bereitstellung - Produktwechsel");
    }

    private static final GraphqlApiLeanix graphqlApiLeanix = new GraphqlApiLeanix();
    private static final Map<String, BusinessActivity> dataToConvertToXls = new HashMap<>();

    public static void main(String[] args) {
        String nameFromPDF = "PVG_TS-0001: Auftragsmanagement - MF - Bereitstellung - Neugeschäft PK";
        String leanixIDFromH2DB = "1f929fba-8232-485e-b3e0-a99cb6659718";

        //String nameFromPDF = "PVG_TS-0002: Auftragsmanagement - MF - Bereitstellung - Bestandsgeschäft PK";
        //String leanixIDFromH2DB = "c8d9a47a-5c55-46c3-b4b5-6ad826f51b03";

        BusinessActivity businessActivity = queryDataAndInstantiateBusinessActivity(nameFromPDF, leanixIDFromH2DB);
        /////////////////////////////////////////////////
        printContentToConsole(businessActivity);

        //ExcelOperations excelWritter = new ExcelOperations("xlsFiles/BCC.xls");
        final List<BusinessActivity> businessActivityList = new ArrayList<>();
        businessActivityList.add(businessActivity);
        ExcelOperations excelWritter = new ExcelOperations("xlsFiles/BCC.xls");

        excelWritter.generateDataFromObject(businessActivityList);
        excelWritter.generateFinalXslFile();
    }

    private static void printContentToConsole(final BusinessActivity businessActivity) {
        System.out.println("business_activity tab: \n" + businessActivity.getBusinessActivityName());

        Set<EnablingService> enablingServiceSet = new HashSet<>(businessActivity.getEnablingServiceList());
        Set<EnablingServiceVariant> enablingServiceVariantSet = new HashSet<>();
        Set<AppDarwinName> businessApplicationNamesSet = new HashSet<>();

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

    private static BusinessActivity queryDataAndInstantiateBusinessActivity(String nameFromPDF, String leanixIDFromH2DB) {
        /***********************1st graphql Query goes directly to the H2 DB****************************/

        graphqlApiLeanix.setGraphqlQueryString(
                IOUtil.getFileContentAsString("src/main/resources/graphql/BusinessCapabilityCatalogue.graphql"));
        Map<String, Map<String, Object>> businessCapabilityCatalogueData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> businessCatalogueSet = parseResultDataForSegment(businessCapabilityCatalogueData, "relToChild");

        System.out.print("Constucting data structure for " + nameFromPDF);
        System.out.println("\t " + leanixIDFromH2DB);

        BusinessActivity businessActivity = new BusinessActivity(leanixIDFromH2DB, nameFromPDF);


        /***********************2nd graphql Query ****************************/
        String esvQuery = IOUtil.getFileContentAsString("src/main/resources/graphql/EnablingServiceVariantQuery.graphql");
        String queryWithLeanixID = StringUtils.replaceOnce(esvQuery, "<leanixID>", leanixIDFromH2DB);
        graphqlApiLeanix.setGraphqlQueryString(queryWithLeanixID);

        Map<String, Map<String, Object>> enablingServiceVariantQueryData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> enablingServiceVariants = parseResultDataForSegment(enablingServiceVariantQueryData, "relBusinessCapabilityToProcess");
        List<EnablingServiceVariant> esvList = new ArrayList<>();

        enablingServiceVariants.forEach(esv -> {
            esvList.add(new EnablingServiceVariant(esv.getLeanixId(), esv.getName()));
        });
        System.out.println("There are " + esvList.size() + " ESV-t for " + nameFromPDF);
        for (EnablingServiceVariant anEnablingServiceVariant : esvList) {

            /***********************3rd graphql Query****************************/
            String enablingServiceVariant = anEnablingServiceVariant.getEnablingServiceVariantName();
            //System.out.println("finding info for : " + enablingServiceVariant);
            String appForEs = IOUtil.getFileContentAsString("src/main/resources/graphql/ApplicationsForES.graphql");
            String appForEsLeainxQuery = StringUtils.replaceOnce(appForEs, "<leanixID>", anEnablingServiceVariant.getEnablingServiceVariantId());
            graphqlApiLeanix.setGraphqlQueryString(appForEsLeainxQuery);

            Map<String, Map<String, Object>> applicationsForESV = graphqlApiLeanix.executeQuery();
            Set<ResultObject> applicationNamesSet = parseResultDataForSegment(applicationsForESV, "relProcessToApplication");
            Set<ResultObject> enablingServicesSet = parseResultDataForSegment(applicationsForESV, "relToParent");

            enablingServicesSet.forEach(es -> {
                EnablingService enablingService = new EnablingService(es.getLeanixId(), es.getName());
                businessActivity.getEnablingServiceList().add(enablingService);
                //System.out.println("\tES: " + es.getName());
                applicationNamesSet.forEach(app -> {
                    AppDarwinName appDarwinName = new AppDarwinName(app.getDisplayName());
                    anEnablingServiceVariant.getAppDarwinNameList().add(appDarwinName);
                    //System.out.println("\t\t" + appDarwinName);
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
            String fullName = (String) factSheetMap.get("fullName");
            String leanixId = (String) factSheetMap.get("id");
            esvSet.add(new ResultObject(name, displayName, leanixId));
        }
        return esvSet;
    }

}

