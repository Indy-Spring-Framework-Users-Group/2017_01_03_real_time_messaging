package com.indysfug;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author russell.scheerer
 */
@Service
@Slf4j
public class ChatHandler {

    public static final String CHAT_MESSAGE_TOPIC = "chat.messages";

    private final List<SseEmitter> emitters = Collections.synchronizedList(new ArrayList<>());
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatHandler(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void addEmitter(SseEmitter emitter) {
        log.info("Adding emitter: {}", emitter.toString());
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
    }

    public void publishMessage(Message message) throws Exception {
        String data = objectMapper.writeValueAsString(new ChatMessageEvent(message));
        rabbitTemplate.convertAndSend(CHAT_MESSAGE_TOPIC, "", data);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = CHAT_MESSAGE_TOPIC, ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC)))
    public void onChatMessageEvent(String message) throws Exception {
        ChatMessageEvent chatMessageEvent = objectMapper.readValue(message, ChatMessageEvent.class);

        log.info("Received ChatMessageEvent from topic: {}", chatMessageEvent);
        broadcastToSse(chatMessageEvent);
    }

    protected void broadcastToSse(ChatMessageEvent chatMessageEvent) {
        log.info("Broadcasting to {} SSE streams", emitters.size());

        List<SseEmitter> emittersToRemove = new ArrayList<>();
        emitters.forEach((SseEmitter emitter) -> {
            try {
                emitter.send(chatMessageEvent, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                log.error("Error publishing to emitter - removing. Reason: {}", e.getMessage());
                emittersToRemove.add(emitter);
                emitter.complete();
            }
        });

        emittersToRemove.forEach(emitter -> emitters.remove(emitter));
    }
}
