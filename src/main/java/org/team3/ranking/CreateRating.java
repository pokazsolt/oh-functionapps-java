package org.team3.ranking;

import java.time.Instant;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class CreateRating {

    private static final String USER_SERVICE_URL_PREFIX = "https://serverlessohapi.azurewebsites.net/api/GetUser?userId=";
    private static final String PRODUCT_SERVICE_URL_PREFIX = "https://serverlessohapi.azurewebsites.net/api/GetProduct?productId=";
    
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
              connectionStringSetting = "AzureCosmosDBConnection",
              createIfNotExists = true)
            OutputBinding<String> outputItem,
            final ExecutionContext context) throws JsonProcessingException {

        context.getLogger().info("Java HTTP trigger processed a request.");



        ObjectMapper mapper = new ObjectMapper();

        String ratingItemStr = request.getBody().orElse(null);

        context.getLogger().info("Received request JSON payload: " + ratingItemStr);

        try {
            RatingItem ratingItem = mapper.readValue(ratingItemStr, RatingItem.class);

            if (ratingItem == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please provide valid ratingItem json").build();
            } else {

                if (isProductIdValid(ratingItem.getProductId()) && isUserIdValid(ratingItem.getUserId()))) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ProductId or userId does not exist.").build();
                } 

                ratingItem.setId(UUID.randomUUID().toString());
                ratingItem.setTimestamp(Instant.now().toString());

                String ratingItemJson = mapper.writeValueAsString(ratingItem);
                context.getLogger().info("RatingItem to be persisted: " + ratingItemJson);
                outputItem.setValue(ratingItemJson);
                
                return request.createResponseBuilder(HttpStatus.OK).body(ratingItemJson).build();
            }

        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Rating should be between 0 and 5").build();
        }
    }


    private boolean isProductIdValid(String produdctId) {
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<Object> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL_PREFIX + produdctId, Object.class);

        return response.getStatusCode().equals(HttpStatus.OK);
    }

    private boolean isUserIdValid(String userId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.getForEntity(USER_SERVICE_URL_PREFIX + userId, Object.class);

        return response.getStatusCode().equals(HttpStatus.OK);
    }
}
