package org.team3.ranking;

import java.time.Instant;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class CreateRating {


    // public static final String CONN_STRING = System.getenv("COSMOS_DB_CONN_STRING").toString();

    /**
     * This function listens at endpoint "/api/CreateRating". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/CreateRating
     * 2. curl {your host}/api/CreateRating?name=HTTP%20Query
     * @throws JsonProcessingException
     */
    @FunctionName("CreateRating")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBOutput(name = "database",
              databaseName = "Ratings",
              collectionName = "RatingItems",
              connectionStringSetting = "AzureCosmosDBConnection")
            OutputBinding<String> outputItem,
            final ExecutionContext context) throws JsonProcessingException {

        context.getLogger().info("Java HTTP trigger processed a request.");

        ObjectMapper mapper = new ObjectMapper();

        String ratingItemStr = request.getBody().orElse(null);

        context.getLogger().info("Received request JSON payload: " + ratingItemStr);

        RatingItem ratingItem = mapper.readValue(ratingItemStr, RatingItem.class);

        if (ratingItem == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please provide valid ratingItem json").build();
        } else {
            ratingItem.setId(UUID.randomUUID().toString());
            ratingItem.setTimestamp(Instant.now().toString());

            String ratingItemJson = mapper.writeValueAsString(ratingItem);
            context.getLogger().info("RatingItem to be persisted: " + ratingItemJson);
            outputItem.setValue(ratingItemJson);
            

            return request.createResponseBuilder(HttpStatus.OK).body(ratingItemJson).build();
        }
    }
}
