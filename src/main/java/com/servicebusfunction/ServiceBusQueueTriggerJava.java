package com.servicebusfunction;

import com.microsoft.azure.functions.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.microsoft.azure.functions.*;
import okhttp3.*;

/**
 * Azure Functions with Service Bus Trigger.
 */
public class ServiceBusQueueTriggerJava {
    /**
     * This function will be invoked when a new message is received at the Service
     * Bus Queue.
     */

    @FunctionName("ServiceBusQueueTriggerJava")
    public void run(
            @ServiceBusQueueTrigger(name = "message", queueName = "myinputqueue", connection = "StorageToIndexer_SERVICEBUS") String message,
            final ExecutionContext context) {
        context.getLogger().info("Java Service Bus Queue trigger function executed.");
        context.getLogger().info(message);

        /*
         * Send the contents of the message to the indexer: In this case the endpoint of
         * the logic app at this URL:
         * https://prod-18.westus2.logic.azure.com:443/workflows/
         * faed76bb5c43460186a9ae81cac85c80/triggers/manual/paths/invoke?api-version=
         * 2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=
         * oDHW9FrpLWUMbF86kudup5ANyUbz6QFQ0HHb-nShxeI
         */
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"messageId\": \"mess-id\",\r\n    \"publishTime\": \"11;33\",\r\n    \"attributes\": {\r\n        \"slb-account-id\": \"tenant1\",\r\n        \"slb-correlation-id\": \"82936003-48a3-414c-9b0c-c0e5d1793fd2\",\r\n        \"slb-data-partition-id\": \"tenant1\"\r\n    },\r\n    \"data\": \"[{\\\"id\\\":\\\"tenant1:wke:wellbore-30015412100001\\\",\\\"kind\\\":\\\"tenant1:wke:wellbore:1.0.0\\\",\\\"op\\\":\\\"create\\\"}]\"\r\n}");
        String endpoint = "https://prod-18.westus2.logic.azure.com:443/workflows/faed76bb5c43460186a9ae81cac85c80/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=oDHW9FrpLWUMbF86kudup5ANyUbz6QFQ0HHb-nShxeI";

        Request request = new Request.Builder()
          .url("https://prod-18.westus2.logic.azure.com:443/workflows/faed76bb5c43460186a9ae81cac85c80/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=oDHW9FrpLWUMbF86kudup5ANyUbz6QFQ0HHb-nShxeI")
          .post(body)
          .addHeader("Content-Type", "application/json")
          .addHeader("User-Agent", "PostmanRuntime/7.13.0")
          .addHeader("Accept", "*/*")
          .addHeader("Cache-Control", "no-cache")
          .addHeader("Postman-Token", "50d709ca-b6b9-4ad4-bd85-1f508f06083b,64c10305-c0fa-4a24-9ec7-e2607520e6f8")
          .addHeader("Host", "prod-18.westus2.logic.azure.com:443")
          .addHeader("accept-encoding", "gzip, deflate")
          .addHeader("content-length", "368")
          .addHeader("Connection", "keep-alive")
          .addHeader("cache-control", "no-cache")
          .build();
        
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}


