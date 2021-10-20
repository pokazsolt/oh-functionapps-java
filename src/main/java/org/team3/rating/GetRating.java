package org.team3.rating;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class GetRating {
    /**
     * This function listens at endpoint "/api/GetRating". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/GetRating
     * 2. curl {your host}/api/GetRating?name=HTTP%20Query
     */
    @FunctionName("GetRating")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(name = "database",
                databaseName = "Ratings",
                collectionName = "RatingItems",
                id = "{Query.ratingId}",
                partitionKey = "{Query.ratingId}",
                connectionStringSetting = "AzureCosmosDBConnection")
                RatingItem ratingItem,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // System.out.println(ratingItem.toString());

        ObjectMapper mapper = new ObjectMapper();

        // context.

        if ( ratingItem == null ) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Rating not found").build();
        }

        try {
            String ratingItemString = mapper.writeValueAsString(ratingItem);
            return request.createResponseBuilder(HttpStatus.FOUND).header("Content-Type", "application/json").body(ratingItemString).build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Error").build();
        }
    }
}
