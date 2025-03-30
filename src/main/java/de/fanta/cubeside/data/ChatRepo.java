package de.fanta.cubeside.data;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

@Entity
public class ChatRepo {
    @Id
    private Integer messageID;
    private String message;
    private Long timestamp;

    public ChatRepo() {
    }

    public ChatRepo(Integer messageID, String message, Long timestamp) {
        this.messageID = messageID;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
