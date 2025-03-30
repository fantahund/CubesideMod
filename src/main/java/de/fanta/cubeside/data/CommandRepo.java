package de.fanta.cubeside.data;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

@Entity
public class CommandRepo {
    @Id
    private Integer commandID;
    private String command;
    private Long timestamp;

    public CommandRepo() {
    }

    public CommandRepo(Integer commandID, String command, Long timestamp) {
        this.commandID = commandID;
        this.command = command;
        this.timestamp = timestamp;
    }

    public Integer getCommandID() {
        return commandID;
    }

    public void setCommandID(Integer messageID) {
        this.commandID = messageID;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String message) {
        this.command = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
