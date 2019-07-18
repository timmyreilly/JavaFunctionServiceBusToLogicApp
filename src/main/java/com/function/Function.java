package com.function;

import java.time.LocalTime;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.localforwarder.library.inputs.contracts.Telemetry;
import com.microsoft.azure.functions.*;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke
     * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/HttpTrigger-Java&code={your function key} 2. curl "{your
     * host}/api/HttpTrigger-Java?name=HTTP%20Query&code={your function key}"
     * Function Key is not needed when running locally, it is used to invoke
     * function deployed to Azure. More details:
     * https://aka.ms/functions_authorization_keys
     */

    public TelemetryClient telemetry = new TelemetryClient();

    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        
        String iKey = TelemetryConfiguration.getActive().getInstrumentationKey(); 


        Long startTime = System.currentTimeMillis(); 

        LocalTime startTimeObj = LocalTime.now();


        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) 
        {
            telemetry.trackException(e);
        }

        LocalTime endTimeObj = LocalTime.now();
        
        Long endTime = System.currentTimeMillis(); 
        Map<String, Double> metrics = new HashMap<>(); 
        metrics.put("ProcessingTime", (double)endTime-startTime); 
        
        metrics.put("StartTime", (double)startTime); 
        metrics.put("EndTime", (double)endTime); 

        Map<String, String> properties = new HashMap<>(); 
        properties.put("DocumentProcessed", "You know me"); 
        properties.put("StartTime", startTimeObj.toString()); 
        properties.put("EndTime", endTimeObj.toString()); 

        telemetry.trackEvent("DocumentProcessed", properties, metrics); 

        

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}
