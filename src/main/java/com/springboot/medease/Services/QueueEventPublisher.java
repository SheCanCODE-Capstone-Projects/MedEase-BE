package com.springboot.medease.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void queueUpdated(String clinicId, String serviceId) {
        String destination = "/queue-updates/" + clinicId + "/" + serviceId;
        messagingTemplate.convertAndSend(destination, "Queue updated"); // send message to subscribers
    }
}
