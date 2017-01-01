package com.indysfug;

import lombok.extern.slf4j.Slf4j;
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

    private final List<SseEmitter> emitters = Collections.synchronizedList(new ArrayList<>());

    public void addEmitter(SseEmitter emitter) {
        log.info("Adding emitter: {}", emitter.toString());
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
    }

    public void publishMessage(Message message) throws Exception {
        broadcastToSse(new ChatMessageEvent(message));
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
