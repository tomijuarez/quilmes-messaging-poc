package com.quilmes.messagingpoc.service;

import com.microsoft.azure.eventgrid.EventGridClient;
import com.microsoft.azure.eventgrid.models.EventGridEvent;
import com.microsoft.azure.management.eventgrid.v2019_01_01.Topic;
import com.quilmes.messagingpoc.model.RegisteredEmployeeEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventDispatcherService {

    private final EventGridClient eventGridClient;

    private final Topic topic;

    private static final String EVENT_TYPE = "Quilmes.Employee.New";
    private static final String EVENT_VERSION = "1.0";

    public void publishEvent(RegisteredEmployeeEvent event) {
        List<EventGridEvent> events = List.of(
                new EventGridEvent(event.getEventId(), "employee", event, EVENT_TYPE, DateTime.now(), EVENT_VERSION));

        try {
            String topicHostName = String.format("https://%s/", new URI(topic.endpoint()).getHost());
            eventGridClient.publishEvents(topicHostName, events);
        } catch (URISyntaxException e) {
            log.error("An unexpected error has occurred: {}", e.getMessage());
        }
    }
}
