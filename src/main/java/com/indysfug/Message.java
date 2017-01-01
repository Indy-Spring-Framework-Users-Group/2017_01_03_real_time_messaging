package com.indysfug;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@ToString
public class Message {

    @NotEmpty
    private String from;

    @NotEmpty
    private String message;

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
    }
}
