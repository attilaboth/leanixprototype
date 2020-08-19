package com.telekom.timon.leanix.leanixapi;

import com.telekom.timon.leanix.util.PropertiesUtil;
import net.leanix.api.GraphqlApi;
import net.leanix.api.common.ApiClient;
import net.leanix.api.common.ApiClientBuilder;
import net.leanix.api.common.ApiException;
import net.leanix.api.common.auth.Authentication;
import net.leanix.api.models.GraphQLRequest;
import net.leanix.api.models.GraphQLResult;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphqlApiLeanix {

    public static AtomicInteger leanixApicall= new AtomicInteger();
    private final ApiClient apiClient;
    public GraphQLRequest graphQLRequest;

    public Map<String, Authentication> getAuthentications() {
        return authentications;
    }

    private Map<String, Authentication> authentications;

    public GraphqlApiLeanix() {
        Properties apiSettings = new PropertiesUtil().getProperties("graphqlApiSettings.properties");

        this.apiClient = new ApiClientBuilder()
                .withBasePath(apiSettings.getProperty("basePath"))
                .withApiToken(apiSettings.getProperty("apiToken"))
                .withTokenProviderHost(apiSettings.getProperty("tokenProvider"))
                .withDebugging(Boolean.FALSE)
                .build();

        authentications = apiClient.getAuthentications();

    }

    public GraphqlApi getGraphqlApi() {
        return new GraphqlApi(apiClient);
    }

    public void setGraphqlQueryString(final String graphqlQueryString) {
        getGraphqlRequest().setQuery(graphqlQueryString);

    }

    public GraphQLRequest getGraphqlRequest() {
        if (graphQLRequest == null) {
            graphQLRequest = new GraphQLRequest();
        }
        return graphQLRequest;
    }

    public Map<String, Map<String, Object>> executeQuery() {
        Map<String, Map<String, Object>> data = new Hashtable<>();
        try {
            GraphQLResult capabilityCatalogueResult = getGraphqlApi().processGraphQL(getGraphqlRequest());

            data = (Map<String, Map<String, Object>>) capabilityCatalogueResult.getData();
        } catch (ApiException apiException) {
            apiException.printStackTrace();
        }
        leanixApicall.incrementAndGet();
        return data;
    }
}
