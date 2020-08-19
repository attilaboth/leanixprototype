package com.telekom.timon.leanix;

import com.google.gson.Gson;
import com.telekom.timon.leanix.datamodel.*;
import com.telekom.timon.leanix.excel.ExcelOperations;
import com.telekom.timon.leanix.leanixapi.GraphqlApiLeanix;
import com.telekom.timon.leanix.restapi.RestApiModel;
import com.telekom.timon.leanix.ucmdbdata.UcmdbDataContainer;
import com.telekom.timon.leanix.util.IOUtil;
import com.telekom.timon.leanix.util.PropertiesUtil;
import net.leanix.api.common.auth.Authentication;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.telekom.timon.leanix.leanixapi.LeanixPototypeConstants.*;

public class LeanixApiPrototypeMain {
    private static final Logger logger = LogManager.getLogger(LeanixApiPrototypeMain.class);

    private static final Map<String, String> testPDFData = new HashMap<>();
    private static final String CONTAINMENT = " | --> | ";
    private static final GraphqlApiLeanix graphqlApiLeanix = new GraphqlApiLeanix();
    private static final Map<String, BusinessActivity> dataToConvertToXls = new HashMap<>();
    //FIXME : remove when in prod
    private static final IOUtil ioUtilInstace = new IOUtil();
    //private static final PerformanceTester performanceWriter = new PerformanceTester();
    private static final PropertiesUtil props = new PropertiesUtil();
    private static final Map<String, Set<String>> businessApplIdsMap; // FIXME: don't use static--- GC problem
    private static final Map<String, Set<String>> darwinNamesMap; //FIXME: don't use static--- GC problem
    private static final Map<String, String> appNameAndBaIsSingularMap; //FIXME: don't use static--- GC problem
    private static String postRequestForAuthentication;
    private static String authorizationValue;

    static {

        businessApplIdsMap = new ExcelOperations("/xlsFilesToBeParsed/ApplicationNamesWithBA_ids.xlsx")
                .getSpecificColumnsBySheetName("Worksheet", 3, 5, false);
        businessApplIdsMap.values().removeIf(Objects::isNull);
        logger.info("initializing businessApplIdsMap...");
        darwinNamesMap = new ExcelOperations("/xlsFilesToBeParsed" +
                "/DarwinNames_itcoNum_applicationNames.xlsx")
                .getSpecificColumnsBySheetName("Application Role", 3, 2, true);
        darwinNamesMap.values().removeIf(Objects::isNull);
        logger.info("initializing darwinNamesMap...");


        // transfering Map<String, Set<String>> into Map<String, String> because businessApplIdsMap contains a
        // Set<String> with 1 elements only
        appNameAndBaIsSingularMap = businessApplIdsMap.entrySet()
                .stream().collect(Collectors.toMap(
                        entry -> entry.getValue().stream().findFirst().get(), Map.Entry::getKey));
        logger.info("initialiting appNameAndBaIsSingularMap...");

        RestApiModel.getInstance();
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        settingProxy();


        String responsetest = testLeanixApiCall();
        System.out.println(responsetest);

        if (args.length == 0 || !args[0].endsWith(".txt") || Character.isDigit(args[0].charAt(0))) {
            System.out.println("Please specify input file name! E.g.: businessActivityNames.txt");
            System.exit(-1);
        }

        final String fileName = args[0];

        Set<ResultObject> allCapabilitiesSet = getCapabilityCatalogue();

        List<BusinessActivity> businessActivityList = new ArrayList<>();

        // (2) TODO: get the url to PROD of leanIX from Jürgen

        // uCMDB REst Api authentication
        postRequestForAuthentication = RestApiModel.getPOSTRequest(RestApiModel.getJsonParamsForAuthentication(),
                RestApiModel.getUrlForAuthentication(), RestApiModel.AUTHORIZATION_VALUE);
        authorizationValue = postRequestForAuthentication.replace("\"", "").split(":")[1].trim();

        businessActivityList = filterBCCCatalogue(allCapabilitiesSet, fileName);

        /*
        allCapabilitiesSet.stream().forEach(resultObject -> {
            System.out.println(resultObject.getName() + " : " + resultObject.getLeanixId());
            businessActivityList.add(getBusinessActivityFromLeanixApi(
                    resultObject.getName(),
                    resultObject.getLeanixId()));
        });
            */

        System.out.println("ucmdb apiCallNum: " + RestApiModel.apiCallNum);
        System.out.println("leanixApicalls: " + GraphqlApiLeanix.leanixApicall);
        ///////////////////// ExcelOperations ////////////////////////////

        ExcelOperations excelWriter = new ExcelOperations(GENERATTED_XLSX_FILE_NAME);
        excelWriter.generateDataFromObject(businessActivityList);
        excelWriter.generateFinalXslFile(true);

        IOUtil.dumpContentToFile(AppDarwinName.getMismatchingApplicationNames(), OUTPUT_DIR +
                "applicationNamesNotFoundInLeanix_xlsx_" + IOUtil.getTimeStamp() + ".txt");

        IOUtil.dumpContentToFile(AppDarwinName.getMismatchingDarwinNames(), OUTPUT_DIR +
                "applicationNamesMismatchedInUcmdb_" + IOUtil.getTimeStamp() + ".txt");

        //performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());
        //performanceWriter.closePerformanceWriter();
    }

    private static String testLeanixApiCall() {
        StringBuilder response = null;
        BufferedReader input = null;

        try {

            URL url = new URL("https://app.leanix.net/services/mtm/v1/oauth2/token");
            HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            postConnection.setDoOutput(true);
            postConnection.setRequestProperty("Authorization", "Basic kmebL5TSQ54UrWrXZqZNbzPODezNRjdaSz7L6rGS");

            OutputStream outputStream = postConnection.getOutputStream();
            //outputStream.write(jsonParam.getBytes());

            int responseCode = postConnection.getResponseCode();
            //System.out.println("POST Response Code :  " + responseCode);
            //System.out.println("POST Response Message : " + postConnection.getResponseMessage());

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                input = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                String inputLine;
                response = new StringBuilder();

                while ((inputLine = input.readLine()) != null) {
                    response.append(inputLine);
                }

                //System.out.println(response.toString());

            } else {
                throw new RuntimeException("Failed : HTTP error code : " + postConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();

    }

    private static List<BusinessActivity> filterBCCCatalogue(final Set<ResultObject> allCapabilitiesSet,
                                                             String fileName) {

        /*
        List<String> businessActivitiesList = Arrays.asList(
                "PVG_TS-0001: Auftragsmanagement - MF - Bereitstellung - Neugeschäft PK",
                "PVG_TS-0002: Auftragsmanagement - MF - Bereitstellung - Bestandsgeschäft PK",
                "PVG_TS-0005: Auftragsmanagement - FN - Bereitstellung - Produktbereitstellung",
                "PVG_TS-0006: Auftragsmanagement - FN - Bereitstellung - Produktwechsel"
        );
        */


        List<String> businessActivitiesList = IOUtil.getFileContentAsList("./" + fileName);

        final List<BusinessActivity> businessActivityList = new ArrayList<>();

        businessActivitiesList.forEach((businessActivityName) -> {
            String prefixOnly = businessActivityName.substring(0, businessActivityName.indexOf(": "));
            //System.out.println("prefixOnly: " + prefixOnly);

            ResultObject foundCapability = allCapabilitiesSet.stream()
                    .filter(aCapability -> aCapability.getPrefixOnly().equalsIgnoreCase(prefixOnly.trim()))
                    .findAny()
                    .orElse(null);

            if (null != foundCapability) {
                businessActivityList.add(getBusinessActivityFromLeanixApi(
                        foundCapability.getName(),
                        foundCapability.getLeanixId()));
                System.out.println("businessActivityName: " + foundCapability.getName());
            }
        });

        return businessActivityList;
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
    private static BusinessActivity getBusinessActivityFromLeanixApi(final String businessActivityName,
                                                                     final String leanixIdFromCache) {
        Instant start = Instant.now();
        BusinessActivity businessActivity = queryDataAndInstantiateBusinessActivity(businessActivityName, leanixIdFromCache);

        //performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());

        return businessActivity;
    }


    //FIXME: refactor this is too long of a method
    private static BusinessActivity queryDataAndInstantiateBusinessActivity(String name, String leanixID) {
        Instant start = Instant.now();

        BusinessActivity businessActivity = new BusinessActivity(leanixID, name);
        String businessActivityName = businessActivity.getBusinessActivityName();

        Optional<Map.Entry<String, String>> foundBaId = appNameAndBaIsSingularMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(businessActivityName))
                .findFirst();

        if (foundBaId.isPresent()) {
            String baID = foundBaId.get().getKey();
            businessActivity.setBusinessActivityExternalId(baID);
            //System.out.println("baID : " + baID);
        } else {
            System.out.println("\tBA_ID is not found for " + businessActivityName);
        }


        /***********************2nd graphql Query ****************************/
        String esvQuery = IOUtil.getFileContentFromResourceStreamBufferedReader(ENABLING_SERVICE_VARIANT_GRAPHQL);
        graphqlApiLeanix.setGraphqlQueryString(StringUtils.replaceOnce(esvQuery, LEAN_IX_ID, leanixID));

        Map<String, Map<String, Object>> enablingServiceVariantQueryData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> enablingServiceVariants = parseResultDataForSegment(enablingServiceVariantQueryData,
                GRAPHQL_QUERY_RELATION_BUSINESS_CAPABILITY_TO_PROCESS);

        List<EnablingServiceVariant> esvList = new ArrayList<>();
        enablingServiceVariants.forEach(esv -> esvList.add(new EnablingServiceVariant(esv.getLeanixId(), esv.getName())));

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

                    Set<String> possibleApplNamesList = darwinNamesMap.get(anEnablingServiceVariant.getEvsId());
                    appDarwinName.getDarwinNameList().addAll(possibleApplNamesList);

                    //TODO: biztos ez után kell az API-t hívni?
                    appDarwinName.setDarwinName(appDarwinName.replaceApplicationNameWithDarwinName());
                    //TODO ide kell az api hívás

                    apiCallForDarwinNames(appDarwinName);

                    anEnablingServiceVariant.getAppDarwinNameList().add(appDarwinName);
                });
                enablingService.getEnablingServiceVariantList().add(anEnablingServiceVariant);
            });
        }
        //performanceWriter.executePerformanceTest(start, new Object() {}.getClass().getEnclosingMethod().getName());
        return businessActivity;
    }

    private static void apiCallForDarwinNames(final AppDarwinName appDarwinName) {
        if (postRequestForAuthentication != null) {
            String appName = appDarwinName.getDarwinName();

            if (!appName.contains("<*")) {
                String appShortName = appName.split(" \\(")[0]; //TODO: ezcsak APLL-nél működik

                String jsonReplacedWithProperAppName = RestApiModel.getJsonParamsForApplicationIds().replace(
                        "APPLICATION_NAME", appShortName);

                String postRequestForApplication =
                        RestApiModel.getPOSTRequest(jsonReplacedWithProperAppName,
                                RestApiModel.getUrlForApplicationIds(),
                                RestApiModel.AUTHORIZATION_VALUE + authorizationValue);

                //GSON is preferred over Jackson, since it is more effective on parsing speed if the
                // environment deals with lots of small JSON files. Jackson, however faster on big JSON files.
                Gson gson = new Gson();
                UcmdbDataContainer ucmdbData = gson.fromJson(postRequestForApplication, UcmdbDataContainer.class);

                AppDarwinName.isDarwinFoundInUcmdb(appDarwinName, ucmdbData);
            }
        }
    }

    private static Set<ResultObject> getCapabilityCatalogue() {
        String fileContentFromResourceStreamBufferedReader =
                IOUtil.getFileContentFromResourceStreamBufferedReader(BUSINESS_CAPABILITY_CATALOGUE_GRAPHQL);

        graphqlApiLeanix.setGraphqlQueryString(fileContentFromResourceStreamBufferedReader);

        Map<String, Map<String, Object>> businessCapabilityCatalogueData = graphqlApiLeanix.executeQuery();
        Set<ResultObject> businessCatalogueSet = parseResultDataForSegment(businessCapabilityCatalogueData, GRAPHQL_QUERY_RELATION_TO_CHILD);

        return businessCatalogueSet;
    }


    private static Set<ResultObject> parseResultDataForSegment(final Map<String, Map<String, Object>> enablingServiceVariantQueryData,
                                                               String nameOfSegment) {
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

