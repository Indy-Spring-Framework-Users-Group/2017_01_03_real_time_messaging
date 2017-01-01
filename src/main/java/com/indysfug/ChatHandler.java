package com.indysfug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * @author russell.scheerer
 */
@Controller
@Slf4j
public class ChatHandler {

    @MessageMapping("/message")
    @SendTo("/topic/chat.messages")
    public ChatMessageEvent publishMessage(Message message) {
        log.info("Received/publishing message on topic: {}", message);
        return new ChatMessageEvent(message);
    }
}
