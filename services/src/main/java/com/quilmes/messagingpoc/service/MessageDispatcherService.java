package com.quilmes.messagingpoc.service;

import com.azure.storage.queue.QueueClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quilmes.messagingpoc.model.ProcessNewEmployeeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MessageDispatcherService {

    private final QueueClient queueClient;

    private final ObjectMapper objectMapper;

    public void produceMessage(ProcessNewEmployeeMessage message) {
        try {
            String stringifiedMessage = objectMapper.writeValueAsString(message);
            queueClient.sendMessage(stringifiedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An unexpected error has occurred: {}", e.getMessage());
        }
    }
}
