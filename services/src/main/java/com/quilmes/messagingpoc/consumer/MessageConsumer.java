package com.quilmes.messagingpoc.consumer;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.QueueMessageItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quilmes.messagingpoc.model.ProcessNewEmployeeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MessageConsumer {

    private final QueueClient queueClient;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void consumeMessages() {
        QueueMessageItem message = queueClient.receiveMessage();
        if (message != null) {
            try {
                ProcessNewEmployeeMessage messagePayload =
                        objectMapper.readValue(message.getMessageText(), ProcessNewEmployeeMessage.class);
                log.info("Processing message: {}", messagePayload);
            } catch (JsonProcessingException e) {
                log.error("Malformed message {}", message.getMessageId());
            } finally {
                queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
            }
        }
    }
}
