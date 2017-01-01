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

    private String userAvatarUrl;
    private String userUrl;

    public Message(String from, String message, String userAvatarUrl, String userUrl) {
        this.from = from;
        this.message = message;
        this.userAvatarUrl = userAvatarUrl;
        this.userUrl = userUrl;
    }
}
