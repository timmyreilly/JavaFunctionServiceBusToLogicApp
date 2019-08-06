package com.topictriggerexample;

import com.microsoft.azure.functions.annotation.*;
import java.io.IOException;
import com.microsoft.azure.functions.*;
import okhttp3.*;


/**
 * Azure Functions with Service Topic Trigger.
 */
public class ServiceBusTopicTriggerJava {
    /**
     * This function will be invoked when a new message is received at the Service Bus Topic.
     */
    @FunctionName("ServiceBusTopicTriggerJava")
    public void run(
        @ServiceBusTopicTrigger(
            name = "message",
            topicName = "microsoft-indexer-queue",
            subscriptionName = "az-function-subscriber",
            connection = "StorageToIndexer_SERVICEBUS"
        )
        String message,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Service Bus Topic trigger function executed.");
        context.getLogger().info(message);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        
        RequestBody bodyFromMessage = RequestBody.create(mediaType, message); 
        
        String defaultEndpoint = "https://prod-18.westus2.logic.azure.com:443/workflows/faed76bb5c43460186a9ae81cac85c80/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=oDHW9FrpLWUMbF86kudup5ANyUbz6QFQ0HHb-nShxeI";
        String endpoint = System.getenv("URL_ENDPOINT") == null ? defaultEndpoint : System.getenv("URL_ENDPOINT"); 
                
        Request request = new Request.Builder()
        .url(endpoint)
        .post(bodyFromMessage)
        .addHeader("Content-Type", "application/json")
        .build();
        
        try {
            Response response = client.newCall(request).execute();
            context.getLogger().info(response.body().source().toString()); 
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}