package com.akdag.akka.keys;


import akka.actor.typed.ActorRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Command {

    private String message;
    private ActorRef<Command> replyTo;

    @JsonCreator
    public Command(@JsonProperty("message") String message,
                   @JsonProperty("replyTo") ActorRef<Command> replyTo) {
        this.message = message;
        this.replyTo = replyTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ActorRef<Command> getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(ActorRef<Command> replyTo) {
        this.replyTo = replyTo;
    }
}
