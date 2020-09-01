package com.quilmes.messagingpoc.controller;

import com.quilmes.messagingpoc.model.ProcessNewEmployeeMessage;
import com.quilmes.messagingpoc.model.RegisteredEmployeeEvent;
import com.quilmes.messagingpoc.service.EventDispatcherService;
import com.quilmes.messagingpoc.service.MessageDispatcherService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hook")
public class HookController {

    private final EventDispatcherService eventDispatcherService;

    private final MessageDispatcherService messageDispatcherService;

    public HookController(EventDispatcherService eventDispatcherService, MessageDispatcherService messageDispatcherService) {
        this.eventDispatcherService = eventDispatcherService;
        this.messageDispatcherService = messageDispatcherService;
    }

    @PostMapping("event")
    public void publishEvent(@RequestBody RegisteredEmployeeEvent employeeEvent) {
        eventDispatcherService.publishEvent(employeeEvent);
    }

    @PostMapping("message")
    public void produceMessage(@RequestBody ProcessNewEmployeeMessage employeeMessage) {
        messageDispatcherService.produceMessage(employeeMessage);
    }
}
