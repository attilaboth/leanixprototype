package com.telekom.timon.leanix;

import net.leanix.api.GraphqlApi;
import net.leanix.api.common.ApiClient;
import net.leanix.api.common.ApiClientBuilder;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.GraphQLRequest;
import net.leanix.api.models.GraphQLResult;

import java.util.List;
import java.util.Map;

public class LeanixApiTest {

    private static final String API_TOKEN = "YmRKJkKrKZGVxGnOJYdPuyKJZFTm7QkC8VY9WvnR";

    public static void main(String[] args) {

        try {


            ApiClient apiClient = new ApiClientBuilder()
                    .withBasePath("https://app.leanix.net/services/pathfinder/v1")
                    .withApiToken(API_TOKEN)
                    .withTokenProviderHost("app.leanix.net")
                    .build();


            GraphqlApi graphqlApi = new GraphqlApi(apiClient);

            String query = "{" +
                    "allFactSheets(filter: {fullTextSearch: \"design\"}) {" +
                    "edges { node {id displayName}}" +
                    "}" +
                    "}";

            GraphQLRequest graphqlRequest = new GraphQLRequest();
            graphqlRequest.setQuery(query);

            GraphQLResult result = graphqlApi.processGraphQL(graphqlRequest);

            Map<String, Map<String, Object>> data = (Map<String, Map<String, Object>>) result.getData();
            List<Map<String, Object>> edgeList = (List<Map<String, Object>>) data.get("allFactSheets").get("edges");

            for (Map<String, Object> edge : edgeList) {
                Map<String, Object> node = (Map<String, Object>) edge.get("node");
                System.out.println(node.get("displayName"));
            }

        } catch (ApiException apiException) {
            apiException.printStackTrace();
        }

    }
}
