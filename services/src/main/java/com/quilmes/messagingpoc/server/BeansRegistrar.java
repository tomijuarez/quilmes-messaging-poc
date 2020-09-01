package com.quilmes.messagingpoc.server;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.microsoft.azure.credentials.AzureCliCredentials;
import com.microsoft.azure.eventgrid.EventGridClient;
import com.microsoft.azure.eventgrid.TopicCredentials;
import com.microsoft.azure.eventgrid.implementation.EventGridClientImpl;
import com.microsoft.azure.management.eventgrid.v2019_01_01.Topic;
import com.microsoft.azure.management.eventgrid.v2019_01_01.implementation.EventGridManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootConfiguration
@EnableScheduling
public class BeansRegistrar {

    @Value("${azure.messaging.resource-group}")
    private String resourceGroup;

    @Value("${azure.messaging.event-grid.topic}")
    private String topicName;

    @Value("${azure.messaging.storage-queue.sas_token}")
    private String storageQueueSasToken;

    @Value("${azure.messaging.storage-queue.queue}")
    private String queueName;

    @Value("${azure.messaging.storage-queue.endpoint}")
    private String storageQueueEndpoint;

    @Bean
    public EventGridManager eventGridManager() throws IOException {
        AzureCliCredentials credentials = AzureCliCredentials.create();
        return EventGridManager.configure().authenticate(
                credentials, credentials.defaultSubscriptionId());
    }

    @Bean
    public Topic topic(EventGridManager eventGridManager) {
        return eventGridManager.topics().getByResourceGroup(resourceGroup, topicName);
    }

    @Bean
    public EventGridClient eventGridClient(EventGridManager eventGridManager) {
        String topicKey = eventGridManager
                .topics()
                .listSharedAccessKeysAsync(resourceGroup, topicName)
                .toBlocking()
                .last()
                .key1();

        TopicCredentials topicCredentials = new TopicCredentials(topicKey);
        return new EventGridClientImpl(topicCredentials);
    }

    @Bean
    public QueueClient queueServiceClient() {
        return new QueueClientBuilder()
                .endpoint(storageQueueEndpoint)
                .sasToken(storageQueueSasToken)
                .queueName(queueName)
                .buildClient();
    }
}
