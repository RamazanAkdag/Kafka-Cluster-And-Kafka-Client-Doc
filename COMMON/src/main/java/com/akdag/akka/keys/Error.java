package com.akdag.akka.keys;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error extends Command {

    @JsonCreator
    public Error(@JsonProperty("message") String message) {
        super(message, null);
    }
}