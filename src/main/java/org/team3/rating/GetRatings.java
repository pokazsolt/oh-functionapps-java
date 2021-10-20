package org.team3.rating;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class GetRatings {
    @FunctionName("GetRatings")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(name = "database",
                databaseName = "Ratings",
                collectionName = "RatingItems",
                sqlQuery = "select * from Ratings r where r.userId = {userId}",
                connectionStringSetting = "AzureCosmosDBConnection")
                RatingItem[] ratingItems,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // System.out.println(ratingItem.toString());

        ObjectMapper mapper = new ObjectMapper();

        // context.

        if ( ratingItems == null ) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Rating not found").build();
        }

        try {
            String ratingItemsString = mapper.writeValueAsString(ratingItems);
            return request.createResponseBuilder(HttpStatus.FOUND).header("Content-Type", "application/json").body(ratingItemsString).build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Error").build();
        }
    }
}
