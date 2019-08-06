
# Notes: 

Endpoint for Elastic: 
http://lb-s72r3une3gbju.westus2.cloudapp.azure.com:9200/

Get all the results out of elastic: 
http://lb-s72r3une3gbju.westus2.cloudapp.azure.com:9200/slbindex/_search/?size=1000&pretty=true 

Third Party Library for HttpClient in Azure Functions: 
https://square.github.io/okhttp/recipes/ 

Editing the host.json to provide some sort of rate limiting from the Azure Function off service bus towards the indexer process. 
https://docs.microsoft.com/en-us/azure/azure-functions/functions-bindings-service-bus#hostjson-settings 


# App Insights Queries: 
```kusto
customEvents
| extend startTime = customMeasurements.['StartTime']
| extend stopTime = customMeasurements.['EndTime']
| project startTime, stopTime, timestamp  
```

```kusto
customEvents
| extend startTime = tolong(customMeasurements.['StartTime'])
| extend stopTime = tolong(customMeasurements.['EndTime'])
| project customMeasurements, startTime, stopTime
| order by startTime asc
```

```kusto
let table3 = customEvents 
| where name == "DocProcessed"
| extend StartTime = todatetime(customDimensions.['StartTime'])
| extend StopTime = todatetime(customDimensions.['EndTime'])
| extend SessionId = customDimensions.['DocumentProcessed']
| project customMeasurements, StartTime, StopTime, SessionId, customDimensions;
table3
| mv-expand samples = range(bin(StartTime, 1s), StopTime, 1s)
| summarize count(SessionId) by bin(todatetime(samples), 1s) 
| order by samples asc
```

# Sample `local.settings.json` 

```json

{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "DefaultEndpointsProtocol=https;AccountName=taskqueueratelib711;AccountKey=ab12secret5H5mFscAl2mr/Utb+9K+mTBlfI7karxMOK+0Lh15OTjdGDRsHX+TmEorgCXlqSECRETTLzIj7NfK/Lw==;EndpointSuffix=core.windows.net",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "StorageToIndexer_SERVICEBUS": "Endpoint=sb://storagetoindexer.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=abcdefpeoWtox6JqdcxoRlRMtRssoIYXFLG3E2bwKKk=", 
    "APPLICATION_INSIGHTS_IKEY" : "efc44cda-da26-49a6-bf94-123e12d08e19", 
    "URL_ENDPOINT" : "https://prod-18.westus2.logic.azure.com:443/workflows/abcd12bb5c43460186a9ae81cac85c80/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=oABC9FrpLWUMbF86kudup5ANyUbz6QFQ0HHb-nShxeI"
  }
}

```