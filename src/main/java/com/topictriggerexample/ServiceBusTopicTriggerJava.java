package com.topictriggerexample;

import com.microsoft.azure.functions.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.microsoft.azure.functions.*;
import okhttp3.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;

/**
 * Azure Functions with Service Topic Trigger.
 */
public class ServiceBusTopicTriggerJava {
    /**
     * This function will be invoked when a new message is received at the Service
     * Bus Topic.
     */

    private static HttpURLConnection connection;

    @FunctionName("ServiceBusTopicTriggerJava")
    public void run(
            @ServiceBusTopicTrigger(name = "message", topicName = "microsoft-indexer-queue", subscriptionName = "az-function-subscriber", connection = "StorageToIndexer_SERVICEBUS") String message,
            final ExecutionContext context) throws IOException {
        context.getLogger().info("Java Service Bus Topic trigger function executed.");
        context.getLogger().info(message);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody bodyFromMessage = RequestBody.create(mediaType, message);

        String defaultEndpoint = "https://prod-18.westus2.logic.azure.com:443/workflows/faed76bb5c43460186a9ae81cac85c80/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=oDHW9FrpLWUMbF86kudup5ANyUbz6QFQ0HHb-nShxeI";
        String endpoint = System.getenv("URL_ENDPOINT") == null ? defaultEndpoint : System.getenv("URL_ENDPOINT");

        Request request = new Request.Builder().url(endpoint).post(bodyFromMessage)
                .addHeader("Content-Type", "application/json").build();

        try {
            Response response = client.newCall(request).execute();
            context.getLogger().info(response.body().source().toString());
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // can we do it with the native java http client:
        String url = defaultEndpoint;
        byte[] postData = message.getBytes(StandardCharsets.UTF_8);

        try {

            URL myUrl = new URL(url);
            connection = (HttpURLConnection) myUrl.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Java client");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());

        } finally {

            connection.disconnect();
        }

    }
}