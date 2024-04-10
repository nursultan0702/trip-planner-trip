package com.tripplannertrip.kafka;

import com.tripplannertrip.model.NotificationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final StreamBridge streamBridge;

    public void sendMessage(NotificationRecord notification) {
        streamBridge.send("output", notification);
    }
}

