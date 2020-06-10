package com.telekom.timon.leanix.leanixapi;

import net.leanix.api.GraphqlApi;
import net.leanix.api.common.ApiClient;
import net.leanix.api.common.ApiClientBuilder;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.GraphQLRequest;
import net.leanix.api.models.GraphQLResult;

import java.util.Hashtable;
import java.util.Map;

public class GraphqlApiLeanix {

    private static final String API_TOKEN = "JYRprc7rfJWYwJxOdzGCTuQTHqVGPL4ge22aAMqu";
    private static final String BASEPATH = "https://telekom.leanix.net/services/pathfinder/v1";
    private static final String TOKEN_PROVIDER = "app.leanix.net";

    private final ApiClient apiClient;
    public GraphQLRequest graphQLRequest;

    public GraphqlApiLeanix() {
        this.apiClient = new ApiClientBuilder()
                .withBasePath(BASEPATH)
                .withApiToken(API_TOKEN)
                .withTokenProviderHost(TOKEN_PROVIDER)
                .withDebugging(Boolean.FALSE)
                .build();
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
        return data;
    }
}
