package com.indysfug;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author russell.scheerer
 */
@Data
public class ChatMessageEvent {
    private Message message;
    private OffsetDateTime messageTime;

    @SuppressWarnings("unused")
    private ChatMessageEvent() {
        this(null);
    }

    public ChatMessageEvent(Message message) {
        this(message, OffsetDateTime.now());
    }

    public ChatMessageEvent(Message message, OffsetDateTime messageTime) {
        this.message = message;
        this.messageTime = messageTime;
    }
}
