package com.telekom.timon.leanix.restapi;

import com.telekom.timon.leanix.util.IOUtil;
import com.telekom.timon.leanix.util.PropertiesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class RestApiModel {
    public static final String AUTHORIZATION_VALUE = "Bearer ";

    private static RestApiModel restApiInstance = null;
    private static String jsonParamsForAuthentication;
    private static String jsonParamsForApplicationIds;
    private static String urlForAuthentication;
    private static String urlForApplicationIds;
    private static HttpURLConnection postConnection;
    private static OutputStream outputStream;
    private static BufferedReader input;
    public static AtomicInteger apiCallNum;

    private RestApiModel() {
        Properties urlProperties = new PropertiesUtil().getProperties("restApiUrlSettings.properties");

        urlForAuthentication = urlProperties.getProperty("urlForAuthentication");
        urlForApplicationIds = urlProperties.getProperty("urlForApplicationIds");

        jsonParamsForAuthentication =
                IOUtil.getFileContentFromResourceStreamBufferedReader("/json/restApiAuthentication.json");
        jsonParamsForApplicationIds =
                IOUtil.getFileContentFromResourceStreamBufferedReader("/json/restApiApplicationIds.json");

        /*jsonParamsForAuthentication = IOUtil.getFileContentAsString(
               "src/main/resources/json/restApiAuthentication.json");
        jsonParamsForApplicationIds = IOUtil.getFileContentAsString(
                "src/main/resources/json/restApiApplicationIds.json");*/

        apiCallNum = new AtomicInteger();
    }

    public static RestApiModel getInstance() {
        if (restApiInstance == null) {
            restApiInstance = new RestApiModel();
        }
        return restApiInstance;
    }

    public static String getJsonParamsForAuthentication() {
        return jsonParamsForAuthentication;
    }

    public static String getJsonParamsForApplicationIds() {
        return jsonParamsForApplicationIds;
    }

    public static String getUrlForAuthentication() {
        return urlForAuthentication;
    }

    public static String getUrlForApplicationIds() {
        return urlForApplicationIds;
    }

    public static String getPOSTRequest(String jsonParam, String urlRequest, String authorizationValue) {
        StringBuilder response = null;

        try {
            URL url = new URL(urlRequest);
            postConnection = (HttpURLConnection) url.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("Content-Type", "application/json");
            postConnection.setDoOutput(true);
            postConnection.setRequestProperty("Authorization", authorizationValue);

            outputStream = postConnection.getOutputStream();
            outputStream.write(jsonParam.getBytes());

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

        apiCallNum.incrementAndGet();
        return response.toString();
    }


    public static void closeApiConnectionAndResources() {
        postConnection.disconnect();

        try {
            outputStream.flush();
            outputStream.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
